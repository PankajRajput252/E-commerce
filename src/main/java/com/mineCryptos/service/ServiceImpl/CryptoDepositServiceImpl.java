package com.mineCryptos.service.ServiceImpl;

import com.google.gson.GsonBuilder;
import com.mineCryptos.FinalException;
import com.mineCryptos.model.FinalResponse;
import com.mineCryptos.model.User;
import com.mineCryptos.model.Util;
import com.mineCryptos.model.entitities.enduser.*;
import com.mineCryptos.repo.UserRepository;
import com.mineCryptos.repo.enduser.CryptoDepositRepository;
import com.mineCryptos.repo.enduser.DepositFundRepository;
import com.mineCryptos.repo.enduser.UserWalletRepository;
import com.mineCryptos.repo.enduser.WithdrawalRequestRepository;
import com.mineCryptos.service.Service.AdminService;
import com.mineCryptos.service.Service.CryptoDepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




import javax.transaction.Transactional;

@Service
public class CryptoDepositServiceImpl implements CryptoDepositService {

    private static final Logger logger = LoggerFactory.getLogger(CryptoDepositServiceImpl.class);

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${nowpayments.api-key}")
    private String apiKey;

    @Value("${nowpayments.base-url}")
    private String baseUrl;

    @Value("${nowpayments.ipn-secret}")
    private String ipnSecret;

    @Autowired
    private CryptoDepositRepository cryptoDepositRepository;
    @Autowired
    private UserWalletRepository userWalletRepository;
    @Autowired
    private DepositFundRepository depositFundRepository;
    @Autowired
    private AdminService adminService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WithdrawalRequestRepository withdrawalRequestRepository;

    @Transactional
    public Map<String, Object> createDeposit(DepositRequest request) {

        String url = baseUrl + "/payment";

        Map<String, Object> payload = new HashMap<>();
        payload.put("price_amount", request.getAmount());
        payload.put("price_currency", "usd");
        payload.put("pay_currency", "bnbbsc");
        payload.put("ipn_callback_url", "http://minecryptos-env.eba-nsbmtw9i.ap-south-1.elasticbeanstalk.com/api/deposit/webhook");

//        payload.put("is_fixed_rate", true);        // locks exact amount in BNB
//        payload.put("is_fee_paid_by_user", true);  // user pays transaction fee
//        payload.put("strict_amount", true);

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<?> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<Map> res = restTemplate.postForEntity(url, entity, Map.class);

        Map<String, Object> response = res.getBody();
        CryptoDeposit deposit = new CryptoDeposit();
        deposit.setPaymentId(response.get("payment_id").toString());
        deposit.setAmount(request.getAmount());
        deposit.setUserNodeId(request.getUserNodeId());
        deposit.setPaymentStatus("PENDING");
        deposit.setPayAddress(response.get("pay_address").toString());
        cryptoDepositRepository.save(deposit);
        if(Util.isDefined(request.getDepositPkId())){
         depositFundRepository.updatePaymentIdBasedOnPkId(deposit.getPaymentId(),request.getUserNodeId() ,  request.getDepositPkId());
        }

        return res.getBody();
    }

    @Transactional
    public Map<String, Object> createDepositV2(DepositRequest req) {

        String payCurrency = mapUserCurrency(req.getSelectedCurrency());

        // Step 1: Convert INR → USD using NowPayments rate
        BigDecimal finalPriceAmount = req.getAmount();

        if("INR".equalsIgnoreCase(req.getSelectedCurrency())) {
            BigDecimal minAmount = getMinAmount("inr", "usdt");

//            if (req.getAmount().compareTo(minAmount) < 0) {
//                Util.setMessage(finalResponse, "100", "Error: Request is already locked or completed.");
//                return finalResponse;
//                throw new RuntimeException("Minimum allowed amount is " + minAmount + " INR");
//
//            }
            BigDecimal rate = fetchConversionRate("INR", "USD");  // OR INR→USDT
            finalPriceAmount = req.getAmount().multiply(rate);


        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("price_amount", finalPriceAmount);
        payload.put("price_currency", "usd");   // keeps consistent
        payload.put("pay_currency", payCurrency);
        payload.put("ipn_callback_url", "http://minecryptos-env.eba-nsbmtw9i.ap-south-1.elasticbeanstalk.com/api/deposit/webhook");

//        payload.put("is_fixed_rate", true);
//        payload.put("strict_amount", true);

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<?> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<Map> res =
                restTemplate.postForEntity(baseUrl + "/payment", entity, Map.class);

        Map<String, Object> response = res.getBody();

        CryptoDeposit deposit = new CryptoDeposit();
        deposit.setPaymentId(response.get("payment_id").toString());
        deposit.setAmount(req.getAmount());  // Original INR/USDT amount user entered
        deposit.setCurrency(req.getSelectedCurrency());
        deposit.setUserNodeId(req.getUserNodeId());
        deposit.setPayAddress(response.get("pay_address").toString());
        deposit.setPaymentStatus("PENDING");
        deposit.setUserNodeId(req.getUserNodeId());

        cryptoDepositRepository.save(deposit);
        if(Util.isDefined(req.getDepositPkId())){
            depositFundRepository.updatePaymentIdBasedOnPkId(deposit.getPaymentId(),req.getUserNodeId() ,  req.getDepositPkId());
        }

        return response;
    }

    private String mapUserCurrency(String currency) {

        switch (currency.toUpperCase()) {
            case "INR":
                return "usdttrc20"; // INR is converted to USDT internally
            case "USDT":
                return "usdttrc20";
            case "BTC":
                return "btc";
            case "BNB":
                return "bnbbsc";
            case "ETH":
                return "eth";
            default:
                return "usdttrc20";
        }
    }

    @Override
    public BigDecimal fetchConversionRate(String from, String to) {

        String url = baseUrl + "/estimate?amount=1&currency_from="
                + from.toLowerCase() + "&currency_to=" + to.toLowerCase();

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        headers.set("Accept", "application/json");

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> res = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                Map.class
        );

        return new BigDecimal(res.getBody().get("estimated_amount").toString());
    }

    public BigDecimal getMinAmount(String from, String to) {

        String url = baseUrl + "/min-amount?currency_from="
                + from.toLowerCase() + "&currency_to=" + to.toLowerCase();

        HttpHeaders headers = new HttpHeaders();
        headers.set("x-api-key", apiKey);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));


        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                Map.class
        );

        return new BigDecimal(response.getBody().get("min_amount").toString());
    }





//    public void processWebhook(Map<String, Object> body, String sig) {
//
//        String json = new Gson().toJson(body);
//        System.out.println("###################json: " + json);
//        String expected = hmacSha512(json, ipnSecret);
//        System.out.println("###################expected: " + expected);
//        if (!expected.equalsIgnoreCase(sig)) {
//            throw new RuntimeException("Invalid NOWPayments Signature");
//        }
//
//        String paymentId = body.get("payment_id").toString();
//        String status = body.get("payment_status").toString();
//        System.out.println("###################body: " + body);
//
//        // Correct TX HASH
//        String txHash = body.get("payin_hash") != null ? body.get("payin_hash").toString() : null;
//
//        if ("finished".equalsIgnoreCase(status)
//                || "confirmed".equalsIgnoreCase(status)
//                || "completed".equalsIgnoreCase(status)) {
//            confirmDeposit(paymentId, txHash);
//        }
//    }


//    public void processWebhook(Map<String, Object> body, String sig, String rawBody) {
//
//        String expected = hmacSha512(rawBody, ipnSecret);
//
//        logger.info("Expected signature: {}", expected);
//        logger.info("Received signature: {}", sig);
//
//        if (sig == null || !expected.equalsIgnoreCase(sig)) {
//            throw new RuntimeException("Invalid NOWPayments Signature");
//        }
//
//        String paymentId = body.get("payment_id").toString();
//
//        // Support both cases
//        String status = body.containsKey("payment_status")
//                ? body.get("payment_status").toString()
//                : body.get("status").toString();
//
//        String txHash = body.get("payin_hash") != null
//                ? body.get("payin_hash").toString()
//                : null;
//
//        logger.info("Payment ID: " + paymentId);
//        logger.info("Status: " + status);
//        logger.info("TxHash: " + txHash);
//
//        if ("finished".equalsIgnoreCase(status)
//                || "confirmed".equalsIgnoreCase(status)
//                || "completed".equalsIgnoreCase(status)) {
//
//            confirmDeposit(paymentId, txHash);
//        }
//    }
//
//
//    private String hmacSha512(String data, String secret) {
//        try {
//            Mac mac = Mac.getInstance("HmacSHA512");
//            SecretKeySpec key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
//            mac.init(key);
//            byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
//            return Base64.getEncoder().encodeToString(rawHmac);
//        } catch (Exception e) {
//            throw new RuntimeException("HMAC error", e);
//        }
//    }


    @Transactional
    public void confirmDeposit(String paymentId, String txHash) {
        CryptoDeposit deposit = cryptoDepositRepository.findByPaymentId(paymentId);

        deposit.setPaymentStatus("SUCCESS");
        deposit.setTxHash(txHash);
        cryptoDepositRepository.save(deposit);

        // Update user wallet
        UserWallet wallet = userWalletRepository.findByActiveStateCodeFkIdAndUserNodeId("ACTIVE", deposit.getUserNodeId());

        if (Util.isDefined(wallet)) {
            wallet.setBalance(wallet.getBalance().add(deposit.getAmount()));
            userWalletRepository.save(wallet);

        } else {
            UserWallet userWallet = new UserWallet(deposit.getUserNodeId(), deposit.getPayAddress(), BigDecimal.ZERO, BigDecimal.ZERO);
            userWallet.setBalance(BigDecimal.ZERO.add(deposit.getAmount()));
            userWalletRepository.save(userWallet);
        }

      FinalResponse finalResponse=  adminService.confirmDeposit(paymentId);
        if (!finalResponse.getStatusCode().equals("200")) {
            return;
        }
    }

    @Override
    public FinalResponse getHistory(String userId) {
        FinalResponse finalResponse=new FinalResponse();
        List<CryptoDeposit> cryptoDepositList=new ArrayList<>();
        cryptoDepositList=  cryptoDepositRepository.findByActiveStateCodeFkIdAndUserNodeId("ACTIVE",userId,null);
        finalResponse.setData(cryptoDepositList);
        return   finalResponse;
    }


    @Override
    public FinalResponse createBtcWithdrawal(String userNodeId, BtcWithdrawRequest req) {
        FinalResponse finalResponse=new FinalResponse();
        // 1. Validate User
        User user = userRepository.findByNodeIdAndActiveStateCodeFkId(userNodeId,"ACTIVE");
        if (Util.isDefined(user)) {
            // 2. Validate KYC status
//            if (!user.isKycVerified()) {
//                throw new RuntimeException("KYC not completed");
//            }

            // 3. Validate wallet balance
            UserWallet wallet = userWalletRepository.findByActiveStateCodeFkIdAndUserNodeId("ACTIVE",userNodeId);
            if (wallet.getBalance().compareTo(req.getAmount()) < 0) {
                throw new RuntimeException("Insufficient balance");
            }

            // 4. Calculate fee (example: 0.0003 BTC)
            BigDecimal networkFee = new BigDecimal("0.0003");
            BigDecimal finalAmount = req.getAmount().subtract(networkFee);

            if (finalAmount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Amount too low after fee deduction");
            }

            // 5. Freeze funds
            wallet.setBalance(wallet.getBalance().subtract(req.getAmount()));
            wallet.setFrozenBalance(wallet.getFrozenBalance().add(req.getAmount()));
            userWalletRepository.save(wallet);

            // 6. Create Withdrawal Request
            WithdrawalRequest withdrawal = new WithdrawalRequest();
            withdrawal.setUserNodeId(userNodeId);
            withdrawal.setCurrencyCode("BTC");
            withdrawal.setAmount(req.getAmount());
            withdrawal.setFeeWork(networkFee);
            withdrawal.setFinalAmount(finalAmount);
            withdrawal.setWalletAddress(req.getBtcAddress());
            withdrawal.setStatus("PENDING");


            withdrawalRequestRepository.save(withdrawal);
            finalResponse.setResponse(withdrawal);
        }
        else{
            throw new FinalException("Invalid user ");

        }
        Util.setSuccessMessage(finalResponse);
       return finalResponse;
    }

//    public void processWithdrawal(WithdrawalRequest req) {
//
//        // call blockchain wallet API to transfer BTC
//        BlockchainResponse res = blockchainApi.sendBTC(
//                req.getBtcAddress(), req.getFinalAmount()
//        );
//
//        if (res.isSuccess()) {
//            req.setStatus("SUCCESS");
//        } else {
//            req.setStatus("FAILED");
//
//            // return money to user's wallet
//            refund(req);
//        }
//
//        withdrawalRepository.save(req);
//    }

//    public void processWebhookRaw(String rawBody, String sig) {
//
//        String expected = hmacSha512(rawBody, ipnSecret);
//
//        System.out.println("SIG FROM NOWPAY: " + sig);
//        System.out.println("SIG GENERATED  : " + expected);
//
//        if (sig == null || sig.equals("test")) {
//            System.out.println("⚠ Skipping signature check for local testing");
//        } else if (!expected.equals(sig)) {
//            System.out.println("❌ Signature mismatch");
//            return;
//        }
//
//
//        Map<String, Object> body = new Gson().fromJson(rawBody, Map.class);
//
//        String paymentId = body.get("payment_id").toString();
//        String status = body.get("payment_status").toString();
//
//        String txHash = body.get("payin_hash") != null ? body.get("payin_hash").toString() : null;
//
//        if ("finished".equalsIgnoreCase(status)
//                || "confirmed".equalsIgnoreCase(status)
//                || "completed".equalsIgnoreCase(status)) {
//
//            confirmDeposit(paymentId, txHash);
//        }
//    }
//
//   @Override
//    public void processWebhook(Map<String, Object> body, String sig, String rawBody) {
//
//        // Use the sorted JSON approach instead of raw body
//        String expected = hmacSha512Sorted(body, ipnSecret);
//
//        logger.info("Expected signature: {}", expected);
//        logger.info("Received signature: {}", sig);
//
//        if (sig == null || !expected.equalsIgnoreCase(sig)) {
//            logger.error("Signature mismatch! Expected: {}, Received: {}", expected, sig);
//            throw new RuntimeException("Invalid NOWPayments Signature");
//        }
//
//        String paymentId = body.get("payment_id").toString();
//
//        // Support both cases
//        String status = body.containsKey("payment_status")
//                ? body.get("payment_status").toString()
//                : body.get("status").toString();
//
//        String txHash = body.get("payin_hash") != null
//                ? body.get("payin_hash").toString()
//                : null;
//
//        logger.info("Payment ID: " + paymentId);
//        logger.info("Status: " + status);
//        logger.info("TxHash: " + txHash);
//
//        if ("finished".equalsIgnoreCase(status)
//                || "confirmed".equalsIgnoreCase(status)
//                || "completed".equalsIgnoreCase(status)) {
//
//            confirmDeposit(paymentId, txHash);
//        }
//    }
//
//    private String hmacSha512Sorted(Map<String, Object> data, String secret) {
//        try {
//            // Sort the map keys and create sorted JSON
//            String sortedJson = createSortedJson(data);
//            logger.info("Sorted JSON for HMAC: {}", sortedJson);
//
//            Mac mac = Mac.getInstance("HmacSHA512");
//            SecretKeySpec key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
//            mac.init(key);
//            byte[] rawHmac = mac.doFinal(sortedJson.getBytes(StandardCharsets.UTF_8));
//
//            // Convert to HEX (not Base64)
//            return bytesToHex(rawHmac);
//        } catch (Exception e) {
//            throw new RuntimeException("HMAC error", e);
//        }
//    }
//
//    private String createSortedJson(Map<String, Object> data) {
//        try {
//            // Use Gson to create sorted JSON
//            Gson gson = new GsonBuilder().create();
//            Map<String, Object> sortedMap = new TreeMap<>(data);
//            return gson.toJson(sortedMap);
//        } catch (Exception e) {
//            throw new RuntimeException("JSON sorting error", e);
//        }
//    }
//
//    private String bytesToHex(byte[] bytes) {
//        StringBuilder hexString = new StringBuilder();
//        for (byte b : bytes) {
//            String hex = Integer.toHexString(0xff & b);
//            if (hex.length() == 1) {
//                hexString.append('0');
//            }
//            hexString.append(hex);
//        }
//        return hexString.toString();
//    }

    @Override
    public void processWebhook(Map<String, Object> body) {

        logger.info("===== PROCESSING NOWPAYMENTS WEBHOOK =====");

        String paymentId     = getString(body, "payment_id");
        String status        = getString(body, "payment_status", "status"); // support both
        String txHash        = getString(body, "payin_hash");

        logger.info("Payment ID : {}", paymentId);
        logger.info("Status     : {}", status);
        logger.info("Tx Hash    : {}", txHash);

        // 🔥 Only confirm when fully paid
        if ("finished".equalsIgnoreCase(status)
                || "confirmed".equalsIgnoreCase(status)
                || "completed".equalsIgnoreCase(status)) {

            logger.info("Payment finished. Confirming deposit...");
            confirmDeposit(paymentId, txHash);
        } else {
            logger.info("Payment not confirmed yet. Current status: {}", status);
        }
    }

    private String getString(Map<String, Object> body, String... keys) {
        for (String key : keys) {
            if (body.containsKey(key) && body.get(key) != null) {
                return String.valueOf(body.get(key));
            }
        }
        return null;
    }

    @Override
   public FinalResponse confirmManually(String paymentId, String txHash){
        FinalResponse finalResponse=new FinalResponse();
        confirmDeposit(paymentId, txHash);
        return finalResponse;
    }
}
