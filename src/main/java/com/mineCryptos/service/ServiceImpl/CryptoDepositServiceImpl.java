package com.mineCryptos.service.ServiceImpl;

import com.mineCryptos.model.FinalResponse;
import com.mineCryptos.model.Util;
import com.mineCryptos.model.entitities.enduser.CryptoDeposit;
import com.mineCryptos.model.entitities.enduser.DepositRequest;
import com.mineCryptos.model.entitities.enduser.UserWallet;
import com.mineCryptos.repo.enduser.CryptoDepositRepository;
import com.mineCryptos.repo.enduser.DepositFundRepository;
import com.mineCryptos.repo.enduser.UserWalletRepository;
import com.mineCryptos.service.Service.AdminService;
import com.mineCryptos.service.Service.CryptoDepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;

import com.google.gson.Gson;






import javax.transaction.Transactional;

@Service
public class CryptoDepositServiceImpl implements CryptoDepositService {

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

    @Transactional
    public Map<String, Object> createDeposit(DepositRequest request) {

        String url = baseUrl + "/payment";

        Map<String, Object> payload = new HashMap<>();
        payload.put("price_amount", request.getAmount());
        payload.put("price_currency", "usd");
        payload.put("pay_currency", "usdtbsc");
        payload.put("ipn_callback_url", "http://minecryptos-env.eba-nsbmtw9i.ap-south-1.elasticbeanstalk.com/api/deposit/webhook");

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
        cryptoDepositRepository.save(deposit);
        if(Util.isDefined(request.getDepositPkId())){
         depositFundRepository.updatePaymentIdBasedOnPkId(deposit.getPaymentId(),request.getUserNodeId() ,  request.getDepositPkId());
        }

        return res.getBody();
    }



    public void processWebhook(Map<String, Object> body, String sig) {

        String json = new Gson().toJson(body);
        String expected = hmacSha512(json, ipnSecret);

        if (!expected.equalsIgnoreCase(sig)) {
            throw new RuntimeException("Invalid NOWPayments Signature");
        }

        String paymentId = body.get("payment_id").toString();
        String status = body.get("payment_status").toString();

        // Correct TX HASH
        String txHash = body.get("payin_hash") != null ? body.get("payin_hash").toString() : null;

        if ("finished".equalsIgnoreCase(status)) {
            confirmDeposit(paymentId, txHash);
        }
    }




    private String hmacSha512(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA512");
            mac.init(secretKeySpec);
            byte[] hmacData = mac.doFinal(data.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hmacData);
        } catch (Exception e) {
            throw new RuntimeException("Error generating HMAC-SHA512 signature", e);
        }
    }


    @Transactional
    public void confirmDeposit(String paymentId, String txHash) {
        CryptoDeposit deposit = cryptoDepositRepository.findByPaymentId(paymentId);

        deposit.setPaymentStatus("SUCCESS");
        deposit.setTxHash(txHash);
        cryptoDepositRepository.save(deposit);

        // Update user wallet
        UserWallet wallet = userWalletRepository.findByActiveStateCodeFkIdAndUserNodeId("ACTIVE", deposit.getUserNodeId());
        if (Util.isDefined(wallet)) {
            wallet.setBalance(wallet.getBalance() + deposit.getAmount());
            userWalletRepository.save(wallet);
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

}
