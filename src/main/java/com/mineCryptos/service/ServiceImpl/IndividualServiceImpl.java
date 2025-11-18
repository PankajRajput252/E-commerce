package com.mineCryptos.service.ServiceImpl;

import com.mineCryptos.model.FinalResponse;
import com.mineCryptos.model.User;
import com.mineCryptos.model.Util;
import com.mineCryptos.model.entitities.IncomeTypeEnum;
import com.mineCryptos.model.entitities.admin.IncomeType;
import com.mineCryptos.model.entitities.admin.RankReward;
import com.mineCryptos.model.entitities.enduser.*;
import com.mineCryptos.repo.CommissionLedgerRepository;
import com.mineCryptos.repo.UserMapRepository;
import com.mineCryptos.repo.UserRepository;
import com.mineCryptos.repo.admin.IncomeTypeRepository;
import com.mineCryptos.repo.enduser.*;
import com.mineCryptos.service.Service.IndividualService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class IndividualServiceImpl implements IndividualService {

    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private IndividualIncomeSummaryRepository individualIncomeSummaryRepository;
    @Autowired
    private MiningPackageRepository miningPackageRepository;
    @Autowired
    private DepositFundRepository depositFundRepository;
    @Autowired
    private WalletTransactionRepository walletTransactionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SupportTicketRepository supportTicketRepository;
    @Autowired
    private WithdrawalRequestRepository withdrawalRequestRepository;
    @Autowired
    private CommissionLedgerRepository comissionLedgerRepository;
    @Autowired
    private IncomeTypeRepository incomeTypeRepository;
    @Autowired
    private UserMapRepository userMapRepository;
    @Autowired
    private IncomeSummaryRepository incomeSummaryRepository;
    @Autowired
    private AccountStatementRepository accountStatementRepository;
    @Autowired
    private BusinessHistoryRepository businessHistoryRepository;
    @Autowired
    private IndividualRankRewardRepository individualRankRewardRepository;
    @Autowired
    private CryptoDepositRepository cryptoDepositRepository;

    @Override
    public FinalResponse getWalletData(Integer inputPkId, String inputFkId, int page, int size, String filterBy, String searchValue) {
        FinalResponse<Wallet> finalResponse = new FinalResponse<>();
        Pageable pageable = Util.getPageable(size, page);
        List<Wallet> walletList = populateWalletView(inputPkId,inputFkId, filterBy,searchValue, pageable);
        int count = populateWalletViewCount(inputPkId, inputFkId, filterBy);
        finalResponse.setData(walletList);
        finalResponse.setCount( count);
        Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    private int populateWalletViewCount(Integer inputPkId, String inputFkId, String filterBy) {
        int count = 0;
        if(!Util.isDefined(filterBy)) {
            filterBy="ACTIVE";
        }
        if (Util.isDefined(inputPkId)) {
            count = walletRepository.countByWalletPkIdAndActiveStateCodeFkId(inputPkId, filterBy);
        }
        else if (Util.isDefined(inputFkId)) {
            count = walletRepository.countByActiveStateCodeFkIdAndUserNodeCode(filterBy, inputFkId);
        }
        else {
            count = walletRepository.countByActiveStateCodeFkId(filterBy);
        }

        return count;
    }

    private List<Wallet> populateWalletView(Integer inputPkId, String inputFkId, String filterBy, String searchValue, Pageable pageable) {
        List<Wallet> walletList = new ArrayList<>();
        if(!Util.isDefined(filterBy)) {
            filterBy="ACTIVE";
        }
        if (Util.isDefined(inputPkId)) {
            Wallet wallet = walletRepository.findByWalletPkIdAndActiveStateCodeFkId(inputPkId, filterBy);
            walletList.add(wallet);
        }
        else if (Util.isDefined(inputFkId)) {
            walletList = walletRepository.findByActiveStateCodeFkIdAndUserNodeCode(filterBy, inputFkId,pageable);
        }
        else {
            walletList = walletRepository.findByActiveStateCodeFkId(filterBy, pageable);
        }
        return walletList;
    }


    @Override
    public FinalResponse addWalletData(Wallet wallet) {
        FinalResponse finalResponse = new FinalResponse();
        String vLastModifiedDateTime = Util.getCurrentUTCTimestampString();
        wallet.setEffectiveDateTime(vLastModifiedDateTime);
        //effective date cannot be greater than present date
        if (Util.compareDate(wallet.getEffectiveDateTime(), vLastModifiedDateTime) > 0) {
            Util.setMessage(finalResponse, "100", "Error: Effective date time cannot be greater than the present moment.");
            return finalResponse;
        }

        Util.setCommonDefaultAttributes(wallet);

        walletRepository.save(wallet);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse updateWalletData(Integer walletId, Wallet wallet) {
        FinalResponse finalResponse = new FinalResponse();
        walletRepository.findById(walletId)
                .map(existing -> {
                    existing.setMineWallet(wallet.getMineWallet());
                    existing.setNodeWallet(wallet.getNodeWallet());
                    existing.setCapitalWallet(wallet.getCapitalWallet());
                    existing.setTotalCredit(wallet.getTotalCredit());
                    existing.setTotalDebit(wallet.getTotalDebit());
                    return walletRepository.save(existing);
                }).orElseThrow(() -> new RuntimeException("Wallet not found"));
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse deleteWalletData(Integer id) {
        FinalResponse finalResponse = new FinalResponse();
        walletRepository.deleteById(id);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }


    @Override
    public FinalResponse getIndividualIncomeSummary(Integer inputPkId, String inputFkId, int page, int size, String filterBy, String searchValue) {
        FinalResponse<IndividualIncomeSummary> finalResponse = new FinalResponse<>();
        Pageable pageable = Util.getPageable(size, page);
        List<IndividualIncomeSummary> individualIncomeSummaryList = populateIndividualIncomeSummaryView(inputPkId,inputFkId, filterBy,searchValue, pageable);
        int count = populateIndividualIncomeSummaryCount(inputPkId, inputFkId, filterBy);
        finalResponse.setData(individualIncomeSummaryList);
        finalResponse.setCount( count);
        Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    private int populateIndividualIncomeSummaryCount(Integer inputPkId, String inputFkId, String filterBy) {
        int count = 0;
        if(!Util.isDefined(filterBy)) {
            filterBy="ACTIVE";
        }
        if (Util.isDefined(inputPkId)) {
            count = individualIncomeSummaryRepository.countByIndividualIncomeSummaryPkIdAndActiveStateCodeFkId(inputPkId, filterBy);
        }
        else if(Util.isDefined(inputFkId)){
            count = individualIncomeSummaryRepository.countByActiveStateCodeFkIdAndUserNodeId(filterBy,inputFkId);
        }
        else {
            count = individualIncomeSummaryRepository.countByActiveStateCodeFkId(filterBy);
        }

        return count;
    }

    private List<IndividualIncomeSummary> populateIndividualIncomeSummaryView(Integer inputPkId, String inputFkId, String filterBy, String searchValue, Pageable pageable) {
        List<IndividualIncomeSummary> individualIncomeSummaryList = new ArrayList<>();
        if(!Util.isDefined(filterBy)) {
            filterBy="ACTIVE";
        }
        if (Util.isDefined(inputPkId)) {
            IndividualIncomeSummary individualIncomeSummary = individualIncomeSummaryRepository.findByIndividualIncomeSummaryPkIdAndActiveStateCodeFkId(inputPkId, filterBy);
            individualIncomeSummaryList.add(individualIncomeSummary);
        }
        else if(Util.isDefined(inputFkId)){
            individualIncomeSummaryList = individualIncomeSummaryRepository.findByActiveStateCodeFkIdAndUserNodeId(filterBy,inputFkId, pageable);
        }
        else {
            individualIncomeSummaryList = individualIncomeSummaryRepository.findByActiveStateCodeFkId(filterBy, pageable);
        }
        return individualIncomeSummaryList;
    }
    @Override
    public FinalResponse addIndividualIncomeSummary(IndividualIncomeSummary individualIncomeSummary) {
        FinalResponse finalResponse = new FinalResponse();
        String vLastModifiedDateTime = Util.getCurrentUTCTimestampString();
        individualIncomeSummary.setEffectiveDateTime(vLastModifiedDateTime);
        //effective date cannot be greater than present date
        if (Util.compareDate(individualIncomeSummary.getEffectiveDateTime(), vLastModifiedDateTime) > 0) {
            Util.setMessage(finalResponse, "100", "Error: Effective date time cannot be greater than the present moment.");
            return finalResponse;
        }

        Util.setCommonDefaultAttributes(individualIncomeSummary);

        individualIncomeSummaryRepository.save(individualIncomeSummary);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse updateIndividualIncomeSummary(Integer id, IndividualIncomeSummary individualIncomeSummary) {
        FinalResponse finalResponse = new FinalResponse();
        individualIncomeSummaryRepository.findById(id)
                .map(existing -> {
                    existing.setServiceGenerationAmount(individualIncomeSummary.getServiceGenerationAmount());
                    existing.setMatchingIncomeAmount(individualIncomeSummary.getMatchingIncomeAmount());
                    existing.setClubIncomeAmount(individualIncomeSummary.getClubIncomeAmount());
                    existing.setRewardIncomeAmount(individualIncomeSummary.getRewardIncomeAmount());
                    existing.setFastTrackBonusAmount(individualIncomeSummary.getFastTrackBonusAmount());
                    existing.setMiningProfitSharingAmount(individualIncomeSummary.getMiningProfitSharingAmount());
                    existing.setMiningGenerationIncomeAmount(individualIncomeSummary.getMiningGenerationIncomeAmount());
                    existing.setNodeBusinessSharingAmount(individualIncomeSummary.getNodeBusinessSharingAmount());
                    return individualIncomeSummaryRepository.save(existing);
                }).orElseThrow(() -> new RuntimeException(" Individual income  not found"));
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse deleteIndividualIncomeSummary(Integer id) {
        FinalResponse finalResponse = new FinalResponse();
        individualIncomeSummaryRepository.deleteById(id);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse getIndividualMiningPackage(String inputPkId, String inputFkId, int page, int size, String filterBy, String searchValue){
        FinalResponse<MiningPackage> finalResponse = new FinalResponse<>();
        Pageable pageable = Util.getPageable(size, page);
        List<MiningPackage> miningPackageList = populateMiningPackageView(inputPkId,inputFkId, filterBy,searchValue, pageable);
        int count = populateMiningPackageCount(inputPkId, inputFkId, filterBy);
        finalResponse.setData(miningPackageList);
        finalResponse.setCount( count);
        Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    private int populateMiningPackageCount(String inputPkId, String inputFkId, String filterBy) {
        int count = 0;
        if(!Util.isDefined(filterBy)) {
            filterBy="ACTIVE";
        }
        if (Util.isDefined(inputPkId) && Util.isDefined(inputFkId)) {
            count = miningPackageRepository.countByModeAndActiveStateCodeFkIdAndUserNodeCode(inputPkId, filterBy,inputFkId);
        }
        else if(Util.isDefined(inputFkId)) {
            count = miningPackageRepository.countByActiveStateCodeFkIdAndUserNodeCode(filterBy,inputFkId);
        }
        else {
            count = miningPackageRepository.countByActiveStateCodeFkId(filterBy);
        }

        return count;
    }

    private List<MiningPackage> populateMiningPackageView(String inputPkId, String inputFkId, String filterBy, String searchValue, Pageable pageable) {
        List<MiningPackage> miningPackageList = new ArrayList<>();
        if(!Util.isDefined(filterBy)) {
            filterBy="ACTIVE";
        }
        if (Util.isDefined(inputPkId) && Util.isDefined(inputFkId)) {
            miningPackageList = miningPackageRepository.findByModeAndActiveStateCodeFkIdAndUserNodeCode(inputPkId, filterBy,inputFkId, pageable);
        }
        else if(Util.isDefined(inputFkId)) {
            miningPackageList = miningPackageRepository.findByActiveStateCodeFkIdAndUserNodeCode(filterBy,inputFkId, pageable);
        }
        else {
            miningPackageList = miningPackageRepository.findByActiveStateCodeFkId(filterBy, pageable);
        }
        return miningPackageList;
    }


    @Override
    public FinalResponse addMiningPackage(MiningPackage miningPackage) {
        FinalResponse finalResponse=new FinalResponse();
        if (miningPackage.getPackageAmount() < 100 || miningPackage.getPackageAmount() % 10 != 0) {
            throw new IllegalArgumentException("Amount must be >= 100 and in multiples of 10");
        }

        // 2. Validate transaction password and OTP
        // (Assume validateTransactionPassword() and validateOtp() are implemented)
//        if (!validateTransactionPassword(request.getUserId(), request.getTransactionPassword())) {
//            throw new IllegalArgumentException("Invalid transaction password");
//        }
//
//        if (!validateOtp(request.getUserId(), request.getOtp())) {
//            throw new IllegalArgumentException("Invalid OTP");
//        }

         miningPackageRepository.save(miningPackage);
        Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    private boolean validateTransactionPassword(String userId, String password) {
        // TODO: integrate with user auth table
        return "mySecurePass".equals(password);
    }

    private boolean validateOtp(String userId, String otp) {
        // TODO: integrate OTP validation logic
        return "894512".equals(otp);
    }

    @Override
    public FinalResponse updateMiningPackage(Integer id, MiningPackage miningPackage) {
        FinalResponse finalResponse = new FinalResponse();
        miningPackageRepository.findById(id)
                .map(existing -> {
                    existing.setUserNodeCode(miningPackage.getUserNodeCode());
                    existing.setPackageAmount(miningPackage.getPackageAmount());
                    existing.setRemarks(miningPackage.getRemarks());
                    existing.setMode(miningPackage.getMode());
                    existing.setTransactionPassword(miningPackage.getTransactionPassword());
                    return miningPackageRepository.save(existing);
                }).orElseThrow(() -> new RuntimeException(" Mining package   not found"));
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse deleteMiningPackage(Integer id) {
        FinalResponse finalResponse = new FinalResponse();
        miningPackageRepository.deleteById(id);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse getIndividualDepositFund(Integer inputPkId, String inputFkId, int page, int size, String filterBy, String searchValue) {
        FinalResponse<DepositFund> finalResponse = new FinalResponse<>();
        Pageable pageable = Util.getPageable(size, page);
        List<DepositFund> depositFundList = populateDepositFundView(inputPkId,inputFkId, filterBy,searchValue, pageable);
        int count = populateDepositFundCount(inputPkId, inputFkId, filterBy);
        finalResponse.setData(depositFundList);
        finalResponse.setCount( count);
        Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    private int populateDepositFundCount(Integer inputPkId, String inputFkId, String filterBy) {
        int count = 0;
        if(!Util.isDefined(filterBy)) {
            filterBy="ACTIVE";
        }
        if (Util.isDefined(inputPkId)) {
            count = depositFundRepository.countByDepositPkIdAndActiveStateCodeFkId(inputPkId, filterBy);
        }
        else if (Util.isDefined(inputFkId)) {
            count = depositFundRepository.countByActiveStateCodeFkIdAndUserNodeCode(filterBy,inputFkId);
        }
        else {
            count = depositFundRepository.countByActiveStateCodeFkId(filterBy);
        }

        return count;
    }

    private List<DepositFund> populateDepositFundView(Integer inputPkId, String inputFkId, String filterBy, String searchValue, Pageable pageable) {
        List<DepositFund> depositFundList = new ArrayList<>();
        if(!Util.isDefined(filterBy)) {
            filterBy="ACTIVE";
        }
        if (Util.isDefined(inputPkId)) {
            DepositFund depositFund = depositFundRepository.findByDepositPkIdAndActiveStateCodeFkId(inputPkId, filterBy);
            depositFundList.add(depositFund);
        }
        else if (Util.isDefined(inputFkId)) {
            depositFundList = depositFundRepository.findByActiveStateCodeFkIdAndUserNodeCode(filterBy,inputFkId, pageable);
        }
        else {
            depositFundList = depositFundRepository.findByActiveStateCodeFkId(filterBy, pageable);
        }
        depositFundList.stream().map((depositFund)->{
           String userName= userRepository.fetchUserNameBasedOnNodeId(depositFund.getUserNodeCode(),"ACTIVE");
            depositFund.setUserName(userName);
            return depositFund;
        }).collect(Collectors.toList());
        return depositFundList;
    }

    @Override
    public FinalResponse addDepositFund(DepositFund depositFund) {
        FinalResponse finalResponse = new FinalResponse();
        String vLastModifiedDateTime = Util.getCurrentUTCTimestampString();
        depositFund.setEffectiveDateTime(vLastModifiedDateTime);
        //effective date cannot be greater than present date
        if (Util.compareDate(depositFund.getEffectiveDateTime(), vLastModifiedDateTime) > 0) {
            Util.setMessage(finalResponse, "100", "Error: Effective date time cannot be greater than the present moment.");
            return finalResponse;
        }

        Util.setCommonDefaultAttributes(depositFund);

        depositFundRepository.save(depositFund);
        finalResponse.setResponse(depositFund);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse updateDepositFund(Integer id, DepositFund depositFund) {
        FinalResponse finalResponse = new FinalResponse();
        depositFundRepository.findById(id)
                .map(existing -> {
                    existing.setUserNodeCode(depositFund.getUserNodeCode());
                    existing.setCurrency(depositFund.getCurrency());
                    existing.setAmount(depositFund.getAmount());
                    existing.setStatus(depositFund.getStatus());
                    existing.setTransactionPassword(depositFund.getTransactionPassword());
                    existing.setConfirmedAt(depositFund.getConfirmedAt());
                    return depositFundRepository.save(existing);
                }).orElseThrow(() -> new RuntimeException(" Mining package   not found"));
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse deleteDepositFund(Integer id) {
        FinalResponse finalResponse = new FinalResponse();
        depositFundRepository.deleteById(id);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse getWalletTransaction(Integer inputPkId, String inputFkId, int page, int size, String filterBy, String searchValue) {
        FinalResponse<WalletTransaction> finalResponse = new FinalResponse<>();
        Pageable pageable = Util.getPageable(size, page);
        List<WalletTransaction> walletTransactionList = populateWalletTransactionView(inputPkId,inputFkId, filterBy,searchValue, pageable);
        int count = populateWalletTransactionCount(inputPkId, inputFkId, filterBy);
        finalResponse.setData(walletTransactionList);
        finalResponse.setCount( count);
        Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    private int populateWalletTransactionCount(Integer inputPkId, String inputFkId, String filterBy) {
        int count = 0;
        if(!Util.isDefined(filterBy)) {
            filterBy="ACTIVE";
        }
        if (Util.isDefined(inputPkId)) {
            count = walletTransactionRepository.countByWalletTxnPkIdAndActiveStateCodeFkId(inputPkId, filterBy);
        }
        else if (Util.isDefined(inputFkId)) {
            count = walletTransactionRepository.countByActiveStateCodeFkIdAndFromUserId(filterBy,inputFkId);
        }
        else {
            count = walletTransactionRepository.countByActiveStateCodeFkId(filterBy);
        }

        return count;
    }

    private List<WalletTransaction> populateWalletTransactionView(Integer inputPkId, String inputFkId, String filterBy, String searchValue, Pageable pageable) {
        List<WalletTransaction> walletTransactionList = new ArrayList<>();
        if(!Util.isDefined(filterBy)) {
            filterBy="ACTIVE";
        }
        if (Util.isDefined(inputPkId)) {
            WalletTransaction walletTransaction = walletTransactionRepository.findByWalletTxnPkIdAndActiveStateCodeFkId(inputPkId, filterBy);
            walletTransactionList.add(walletTransaction);
        }
        else if (Util.isDefined(inputFkId)) {
            walletTransactionList = walletTransactionRepository.findByActiveStateCodeFkIdAndFromUserId(filterBy,inputFkId, pageable);
        }
        else {
            walletTransactionList = walletTransactionRepository.findByActiveStateCodeFkId(filterBy, pageable);
        }
        walletTransactionList.stream().map((walletTransaction)->{
            String userName= userRepository.fetchUserNameBasedOnNodeId(walletTransaction.getFromUserId(),"ACTIVE");
            walletTransaction.setUserName(userName);
            return walletTransaction;
        }).collect(Collectors.toList());
        return walletTransactionList;
    }
    @Override
    public FinalResponse addWalletTransaction(WalletTransaction walletTransaction) {
        FinalResponse finalResponse = new FinalResponse();
        String vLastModifiedDateTime = Util.getCurrentUTCTimestampString();
        walletTransaction.setEffectiveDateTime(vLastModifiedDateTime);
        //effective date cannot be greater than present date
        if (Util.compareDate(walletTransaction.getEffectiveDateTime(), vLastModifiedDateTime) > 0) {
            Util.setMessage(finalResponse, "100", "Error: Effective date time cannot be greater than the present moment.");
            return finalResponse;
        }

        Util.setCommonDefaultAttributes(walletTransaction);

        walletTransactionRepository.save(walletTransaction);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse updateWalletTransaction(Integer id, WalletTransaction walletTransaction) {
        FinalResponse finalResponse = new FinalResponse();
        walletTransactionRepository.findById(id)
                .map(existing -> {
                    existing.setTransactionId(walletTransaction.getTransactionId());
                    existing.setFromUserId(walletTransaction.getFromUserId());
                    existing.setToUserId(walletTransaction.getToUserId());
                    existing.setFromWallet(walletTransaction.getFromWallet());
                    existing.setToWallet(walletTransaction.getToWallet());
                    existing.setAmount(walletTransaction.getAmount());
                    existing.setStatus(walletTransaction.getStatus());
                    existing.setRemarks(walletTransaction.getRemarks());
                    existing.setConfirmedAt(walletTransaction.getConfirmedAt());
                    return walletTransactionRepository.save(existing);
                }).orElseThrow(() -> new RuntimeException(" Wallet Txn    not found"));
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse deleteWalletTransaction(Integer id) {
        FinalResponse finalResponse = new FinalResponse();
        walletTransactionRepository.deleteById(id);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse getAllDirectMember(String inputPkId, String inputFkId, int page, int size, String filterBy, String searchValue){
        FinalResponse<MemberDetail> finalResponse = new FinalResponse<>();
        Pageable pageable = Util.getPageable(size, page);
        List<MemberDetail> memberDetailList = populateMemberDetailView(inputPkId,inputFkId, filterBy,searchValue, pageable);
        int count = populateMemberDetailCount(inputPkId, inputFkId, filterBy,searchValue);
        finalResponse.setData(memberDetailList);
        finalResponse.setCount( count);
        Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    private int populateMemberDetailCount(String inputPkId, String inputFkId, String filterBy,String searchValue) {
        int count = 0;
        if(!Util.isDefined(filterBy)) {
            filterBy="ACTIVE";
        }
        if (Util.isDefined(inputPkId)) {
            count = userRepository.countByParentNodeIdAndActiveStateCodeFkId(inputPkId, filterBy);
        }
        else if(Util.isDefined(searchValue)){
            count=userRepository.countByActiveStateCodeFkIdAndParentNodeIdContaining(filterBy,searchValue);
        }
        else {
            count = userRepository.countByActiveStateCodeFkId(filterBy);
        }
        return count;
    }

    private List<MemberDetail> populateMemberDetailView(String inputPkId, String inputFkId, String filterBy, String searchValue, Pageable pageable) {
        List<MemberDetail> memberDetailList = new ArrayList<>();
        if(!Util.isDefined(filterBy)) {
            filterBy="ACTIVE";
        }
        List<User> userList=new ArrayList<>();
        if (Util.isDefined(inputPkId)) {
             userList = userRepository.findByParentNodeIdAndActiveStateCodeFkId(inputPkId, filterBy,pageable);
        }
        else if(Util.isDefined(searchValue)){
            userList=userRepository.findByActiveStateCodeFkIdAndParentNodeIdContaining(filterBy,searchValue,pageable);
        }
        else {
            userList = userRepository.findByActiveStateCodeFkId(filterBy, pageable);
        }
        if(Util.isDefined(userList)){
            AtomicInteger levelCount = new AtomicInteger(0);
            userList.stream().map((user)->{
                MemberDetail memberDetail = new MemberDetail();
                memberDetail.setMemberId(user.getNodeId());
                memberDetail.setMemberLevel(1); // increments each time
                memberDetail.setMemberName(user.getName());
                memberDetail.setMemberEmail(user.getEmail());
                memberDetail.setId(user.getUserPkId());
                memberDetail.setActivationDate(user.getDateOfActivation());
                memberDetail.setJoiningDate(user.getDateOfActivation());
                memberDetail.setParentId(user.getParentNodeId());
                memberDetail.setStatus(user.getUserStatus());
                memberDetail.setPosition(user.getPosition());
                memberDetailList.add(memberDetail);
                return user;
            }).collect(Collectors.toList());
        }
        User userData=userRepository.findByNodeIdAndActiveStateCodeFkId(inputPkId, filterBy);
        if(Util.isDefined(userData)){
            MemberDetail memberDetail = new MemberDetail();
            memberDetail.setMemberId(userData.getNodeId());
            memberDetail.setMemberLevel(0); // increments each time
            memberDetail.setMemberName(userData.getName());
            memberDetail.setMemberEmail(userData.getEmail());
            memberDetail.setId(userData.getUserPkId());
            memberDetail.setActivationDate(userData.getDateOfActivation());
            memberDetail.setJoiningDate(userData.getDateOfActivation());
            memberDetail.setParentId(userData.getParentNodeId());
            memberDetail.setStatus(userData.getUserStatus());
            memberDetail.setPosition(userData.getPosition());
            memberDetailList.add(memberDetail);
        }

        return memberDetailList;
    }

    @Override
    public  FinalResponse getTeamHierarchy(String inputPkId, String inputFkId, int page, int size, String filterBy, String searchValue){
        FinalResponse<MemberDetail> finalResponse = new FinalResponse<>();
        Pageable pageable = Util.getPageable(size, page);
        List<MemberDetail> team=new ArrayList<>();
        User userData=userRepository.findByNodeIdAndActiveStateCodeFkId(inputPkId, filterBy);
        if(Util.isDefined(userData)){
            MemberDetail memberDetail = new MemberDetail();
            memberDetail.setMemberId(userData.getNodeId());
            memberDetail.setMemberLevel(0); // increments each time
            memberDetail.setMemberName(userData.getName());
            memberDetail.setMemberEmail(userData.getEmail());
            memberDetail.setId(userData.getUserPkId());
            memberDetail.setActivationDate(userData.getDateOfActivation());
            memberDetail.setJoiningDate(userData.getDateOfActivation());
            memberDetail.setParentId(userData.getParentNodeId());
            memberDetail.setStatus(userData.getUserStatus());
            memberDetail.setPosition(userData.getPosition());
            team.add(memberDetail);
        }
        fetchTeamRecursive(inputPkId, 0, team);
        finalResponse.setData(team);
        finalResponse.setCount(team.size());
        Util.setSuccessMessage(finalResponse);
        return finalResponse;

    }


    private void fetchTeamRecursive(String parentId, int level, List<MemberDetail> team) {
//        if (level >= 10) return; // stop after level 10 (for safety)

        List<User> userList = userRepository.findByParentNodeIdAndActiveStateCodeFkId(parentId, "ACTIVE",null);
        if (userList.isEmpty()) return; // no more downline → stop

        for (User child : userList) {
            MemberDetail detail = new MemberDetail();
            detail.setMemberId(child.getNodeId());
            detail.setMemberLevel(level + 1);
            detail.setMemberName(child.getName());
            detail.setMemberEmail(child.getEmail());
            detail.setPosition(child.getPosition());
            detail.setId(child.getUserPkId());
            detail.setActivationDate(child.getDateOfActivation());
            detail.setJoiningDate(child.getDateOfActivation());
            detail.setParentId(child.getParentNodeId());
            detail.setStatus(child.getUserStatus());
            team.add(detail);
            fetchTeamRecursive(child.getNodeId(), level + 1, team);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public FinalResponse updateProfile(Profile profile) {
        FinalResponse finalResponse = new FinalResponse();

        User user = userRepository.findByNodeIdAndActiveStateCodeFkId(profile.getUserNodeId(), "ACTIVE");

        if (Util.isDefined(user)) {
            user.setName(profile.getUserName());
            user.setEmail(profile.getEmail());
            user.setCountry(profile.getCountry());
            user.setMobile(profile.getMobile());
            user.setTransactionPassword(profile.getTransactionPassword());

            userRepository.save(user);
            finalResponse = Util.setSuccessMessage(finalResponse);
        } else {
            throw new RuntimeException("User not found");
        }

        return finalResponse;
    }


    @Override
    public FinalResponse getSupportTicket(Integer inputPkId, String inputFkId, int page, int size, String filterBy, String searchValue) {
        FinalResponse<SupportTicket> finalResponse = new FinalResponse<>();
        Pageable pageable = Util.getPageable(size, page);
        List<SupportTicket> supportTicketList = populateSupportTicketView(inputPkId,inputFkId, filterBy,searchValue, pageable);
        int count = populateSupportTicketCount(inputPkId, inputFkId, filterBy);
        finalResponse.setData(supportTicketList);
        finalResponse.setCount( count);
        Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }



    private int populateSupportTicketCount(Integer inputPkId, String inputFkId, String filterBy) {
        int count = 0;
        if(!Util.isDefined(filterBy)) {
            filterBy="ACTIVE";
        }
        if (Util.isDefined(inputPkId)) {
            count = supportTicketRepository.countBySupportTicketPkIdAndActiveStateCodeFkId(inputPkId, filterBy);
        }
        else if(Util.isDefined(inputFkId)) {
            count = supportTicketRepository.countByActiveStateCodeFkIdAndUserNodeId(filterBy, inputFkId);
        }
        else {
            count = supportTicketRepository.countByActiveStateCodeFkId(filterBy);
        }

        return count;
    }

    private List<SupportTicket> populateSupportTicketView(Integer inputPkId, String inputFkId, String filterBy, String searchValue, Pageable pageable) {
        List<SupportTicket> supportTicketList = new ArrayList<>();
        if (Util.isDefined(inputPkId)) {
            SupportTicket supportTicket = supportTicketRepository.findBySupportTicketPkIdAndActiveStateCodeFkId(inputPkId, filterBy);
            supportTicketList.add(supportTicket);
        }
        else if(Util.isDefined(inputFkId)) {
            supportTicketList = supportTicketRepository.findByActiveStateCodeFkIdAndUserNodeId(filterBy, inputFkId,pageable);
        }
        else {
            supportTicketList = supportTicketRepository.findByActiveStateCodeFkId(filterBy, pageable);
        }
        supportTicketList.stream().map((supportTicket)->{
            String userName= userRepository.fetchUserNameBasedOnNodeId(supportTicket.getUserNodeId(),filterBy);
            supportTicket.setUserName(userName);
            return supportTicket;
        }).collect(Collectors.toList());
       return supportTicketList;
    }

    @Override
    public FinalResponse addSupportTicket(SupportTicket supportTicket) {
        FinalResponse finalResponse = new FinalResponse();
        String vLastModifiedDateTime = Util.getCurrentUTCTimestampString();
        supportTicket.setEffectiveDateTime(vLastModifiedDateTime);
        //effective date cannot be greater than present date
        if (Util.compareDate(supportTicket.getEffectiveDateTime(), vLastModifiedDateTime) > 0) {
            Util.setMessage(finalResponse, "100", "Error: Effective date time cannot be greater than the present moment.");
            return finalResponse;
        }

        Util.setCommonDefaultAttributes(supportTicket);

        supportTicketRepository.save(supportTicket);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return  finalResponse;
    }

    @Override
    public FinalResponse updateSupportTicket(Integer id, SupportTicket supportTicket) {
        FinalResponse finalResponse = new FinalResponse();
        supportTicketRepository.findById(id)
                .map(existing -> {
                    existing.setCategory(supportTicket.getCategory());
                    existing.setPriority(supportTicket.getPriority());
                    existing.setUserNodeId(supportTicket.getUserNodeId());
                    existing.setMessage(supportTicket.getMessage());
                    existing.setStatus(supportTicket.getStatus());
                    return supportTicketRepository.save(existing);
                }).orElseThrow(() -> new RuntimeException(" Support ticket  not found"));
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse deleteSupportTicket(Integer id) {
        FinalResponse finalResponse = new FinalResponse();
        supportTicketRepository.deleteById(id);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse getWithdrawalRequest(Integer inputPkId, String inputFkId, int page, int size, String filterBy, String searchValue) {
        FinalResponse<WithdrawalRequest> finalResponse = new FinalResponse<>();
        Pageable pageable = Util.getPageable(size, page);
        List<WithdrawalRequest> withdrawalRequestList = populateWithdrawalRequestView(inputPkId,inputFkId, filterBy,searchValue, pageable);
        int count = populateWithdrawalRequestCount(inputPkId, inputFkId, filterBy);
        finalResponse.setData(withdrawalRequestList);
        finalResponse.setCount( count);
        Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }



    private int populateWithdrawalRequestCount(Integer inputPkId, String inputFkId, String filterBy) {
        if(!Util.isDefined(filterBy)) {
            filterBy="ACTIVE";
        }
        int count = 0;
        if (Util.isDefined(inputPkId)) {
            count = withdrawalRequestRepository.countByWithdrawalRequestPkIdAndActiveStateCodeFkId(inputPkId, filterBy);
        }
        else if (Util.isDefined(inputFkId)) {
            count = withdrawalRequestRepository.countByActiveStateCodeFkIdAndUserNodeId(filterBy,inputFkId);
        }
        else {
            count = withdrawalRequestRepository.countByActiveStateCodeFkId(filterBy);
        }

        return count;
    }

    private List<WithdrawalRequest> populateWithdrawalRequestView(Integer inputPkId, String inputFkId, String filterBy, String searchValue, Pageable pageable) {
        List<WithdrawalRequest> withdrawalRequestList = new ArrayList<>();
        if(!Util.isDefined(filterBy)) {
            filterBy="ACTIVE";
        }
        if (Util.isDefined(inputPkId)) {
            WithdrawalRequest withdrawalRequest = withdrawalRequestRepository.findByWithdrawalRequestPkIdAndActiveStateCodeFkId(inputPkId, filterBy);
            withdrawalRequestList.add(withdrawalRequest);
        }
        else if(Util.isDefined(inputFkId)){
            withdrawalRequestList = withdrawalRequestRepository.findByActiveStateCodeFkIdAndUserNodeId(filterBy,inputFkId, pageable);
        }
        else {
            withdrawalRequestList = withdrawalRequestRepository.findByActiveStateCodeFkId(filterBy, pageable);
        }
        return withdrawalRequestList;
    }


    @Override
    public FinalResponse addWithDrawalRequest(WithdrawalRequest withdrawalRequest) {
        FinalResponse finalResponse = new FinalResponse();
        String vLastModifiedDateTime = Util.getCurrentUTCTimestampString();
        withdrawalRequest.setEffectiveDateTime(vLastModifiedDateTime);
        //effective date cannot be greater than present date
        if (Util.compareDate(withdrawalRequest.getEffectiveDateTime(), vLastModifiedDateTime) > 0) {
            Util.setMessage(finalResponse, "100", "Error: Effective date time cannot be greater than the present moment.");
            return finalResponse;
        }

        Util.setCommonDefaultAttributes(withdrawalRequest);

        withdrawalRequestRepository.save(withdrawalRequest);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return  finalResponse;
    }

    @Override
    public FinalResponse updateWithDrawalRequest(Integer id, WithdrawalRequest withdrawalRequest) {
        FinalResponse finalResponse = new FinalResponse();
        withdrawalRequestRepository.findById(id)
                .map(existing -> {
                    existing.setUsername(withdrawalRequest.getUsername());
                    existing.setUserNodeId(withdrawalRequest.getUserNodeId());
                    existing.setWalletType(withdrawalRequest.getWalletType());
                    existing.setWalletAddress(withdrawalRequest.getWalletAddress());
                    existing.setAmount(withdrawalRequest.getAmount());
                    existing.setStatus(withdrawalRequest.getStatus());
                    return withdrawalRequestRepository.save(existing);
                }).orElseThrow(() -> new RuntimeException(" Withdrawal Request  not found"));
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse deleteWithDrawalRequest(Integer id) {
        FinalResponse finalResponse = new FinalResponse();
        withdrawalRequestRepository.deleteById(id);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse getCommissionLedger(Integer inputPkId, String inputFkId, int page, int size, String filterBy, String searchValue) {
        FinalResponse<CommissionLedger> finalResponse = new FinalResponse<>();
        Pageable pageable = Util.getPageable(size, page);
        List<CommissionLedger> commissionLedgerList = populateCommissionLedgerView(inputPkId,inputFkId, filterBy,searchValue, pageable);
        int count = populateCommissionLedgerCount(inputPkId, inputFkId, filterBy);
        finalResponse.setData(commissionLedgerList);
        finalResponse.setCount( count);
        Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }



    private int populateCommissionLedgerCount(Integer inputPkId, String inputFkId, String filterBy) {
        int count = 0;
        if(!Util.isDefined(filterBy)) {
            filterBy="ACTIVE";
        }
        if (Util.isDefined(inputPkId)) {
            count = comissionLedgerRepository.countByCommissionLedgerPkIdAndActiveStateCodeFkId(inputPkId, filterBy);
        }
        else if (Util.isDefined(inputFkId)) {
            count = comissionLedgerRepository.countByActiveStateCodeFkIdAndUserNodeId(filterBy,inputFkId);
        }
        else {
            count = comissionLedgerRepository.countByActiveStateCodeFkId(filterBy);
        }

        return count;
    }

    private List<CommissionLedger> populateCommissionLedgerView(Integer inputPkId, String inputFkId, String filterBy, String searchValue, Pageable pageable) {
        List<CommissionLedger> commissionLedgerList = new ArrayList<>();
        if (Util.isDefined(inputPkId)) {
            CommissionLedger commissionLedger = comissionLedgerRepository.findByCommissionLedgerPkIdAndActiveStateCodeFkId(inputPkId, filterBy);
            commissionLedgerList.add(commissionLedger);
        }
        else if(Util.isDefined(inputFkId)){
            commissionLedgerList = comissionLedgerRepository.findByActiveStateCodeFkIdAndUserNodeId(filterBy,inputFkId, pageable);
        }
        else {
            commissionLedgerList = comissionLedgerRepository.findByActiveStateCodeFkId(filterBy, pageable);
        }
        return commissionLedgerList;
    }

    @Override
    public FinalResponse addCommissionLedger(CommissionLedger commissionLedger) {
        FinalResponse finalResponse = new FinalResponse();
        String vLastModifiedDateTime = Util.getCurrentUTCTimestampString();
        commissionLedger.setEffectiveDateTime(vLastModifiedDateTime);
        //effective date cannot be greater than present date
        if (Util.compareDate(commissionLedger.getEffectiveDateTime(), vLastModifiedDateTime) > 0) {
            Util.setMessage(finalResponse, "100", "Error: Effective date time cannot be greater than the present moment.");
            return finalResponse;
        }

        Util.setCommonDefaultAttributes(commissionLedger);

        comissionLedgerRepository.save(commissionLedger);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return  finalResponse;
    }

    @Override
    public FinalResponse updateCommissionLedger(Integer id, CommissionLedger commissionLedger) {
        FinalResponse finalResponse = new FinalResponse();
        comissionLedgerRepository.findById(id)
                .map(existing -> {
                    existing.setUserNodeId(commissionLedger.getUserNodeId());
                    existing.setIncomeType(commissionLedger.getIncomeType());
                    existing.setIsSettled(commissionLedger.getIsSettled());
                    existing.setAmount(commissionLedger.getAmount());
                    return comissionLedgerRepository.save(existing);
                }).orElseThrow(() -> new RuntimeException(" Commission Ledger  not found"));
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse deleteCommissionLedger(Integer id) {
        FinalResponse finalResponse = new FinalResponse();
        comissionLedgerRepository.deleteById(id);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }


    // General entry point: process an event (purchase/mining payout/fast-track etc.)
    public void processServicePurchase(String buyerId, BigDecimal amount) {
        User buyer = userRepository.findByNodeIdAndActiveStateCodeFkId(buyerId,"ACTIVE");

       // 1. Direct service generation: pay sponsor(s) according to direct rule
        payDirectReferral(buyer, amount);

        // 2. Trigger matching income calculation up the upline
        payMatchingIncome(buyer);

       // 3. Update binary volume if applicable (example)
        updateBinaryVolume(buyer, amount);


    }


    private void payDirectReferral(User buyer, BigDecimal amount) {
        User sponsoredParent = userRepository.findByParentNodeIdAndActiveStateCodeFkId(buyer.getParentNodeId(),"ACTIVE");
        if (sponsoredParent == null) return;


        List<IncomeType> rules = incomeTypeRepository.findByIncomeTypeCodeAndActiveStateCodeFkId(IncomeTypeEnum.SERVICE_GENERATION,"ACTIVE");
        // assume one rule only for direct service
        IncomeType rule = rules.stream().findFirst().orElse(null);
        if (rule == null) return;


        BigDecimal payout = calculateByRule(rule, amount);
        // now updating commision to parentId
        saveLedger(sponsoredParent.getNodeId(), String.valueOf(IncomeTypeEnum.SERVICE_GENERATION), payout, "Direct referral from " + buyer.getNodeId());
    }


    private void updateBinaryVolume(User buyer, BigDecimal amount) {
// This is a simple example: propagate volume to sponsors as left/right randomly
        UserMap sponsoredParent = userMapRepository.findByUserNodeIdAndActiveStateCodeFkId(buyer.getParentNodeId(), "ACTIVE");
        if (sponsoredParent == null) return;
// for demo: add to leftVolume
        sponsoredParent.setLeftVolume(sponsoredParent.getLeftVolume().add(amount));
        userMapRepository.save(sponsoredParent);
    }


    private void payMatchingIncome(User buyer) {
       // traverse upline and check left/right match
        UserMap upline = userMapRepository.findByUserNodeIdAndActiveStateCodeFkId(buyer.getParentNodeId(), "ACTIVE");
        while (upline != null) {
            BigDecimal left = upline.getLeftVolume();
            BigDecimal right = upline.getRightVolume();
            BigDecimal matched = left.min(right);
            if (matched.compareTo(BigDecimal.ZERO) > 0) {
       // fetch matching rules
                List<IncomeType> rules = incomeTypeRepository.findByIncomeTypeCodeAndActiveStateCodeFkId(IncomeTypeEnum.MATCHING_INCOME,"ACTIVE");
                IncomeType rule = rules.stream().findFirst().orElse(null);
                if (rule != null) {
                    BigDecimal payout = calculateByRule(rule, matched);
                    saveLedger(upline.getUserNodeId(), String.valueOf(IncomeTypeEnum.MATCHING_INCOME), payout, "Pair matched: " + matched);


                  // subtract matched volume
                    upline.setLeftVolume(left.subtract(matched));
                    upline.setRightVolume(right.subtract(matched));
                    userMapRepository.save(upline);
                }
            }
//            upline = upline.getSponsor();
            String parentId = userRepository.fetchParentNodeBasedOnUserNodeId(upline.getUserNodeId(),"ACTIVE");
            upline = userMapRepository.findByUserNodeIdAndActiveStateCodeFkId(parentId, "ACTIVE");
        }
    }


    // Club income distribution (e.g., monthly pool)  This is based on user
    public void distributeClubPool(BigDecimal totalPool) {
        // find qualified members
        List<User> allUsers = userRepository.findAll();
        List<User> qualified = allUsers.stream()
                .filter(u -> qualifiesForClub(u))
                .collect(Collectors.toList());
        if (qualified.isEmpty()) return;
        BigDecimal share = totalPool.divide(BigDecimal.valueOf(qualified.size()), 2, BigDecimal.ROUND_HALF_UP);
        for (User u : qualified) {
            saveLedger(u.getNodeId(), String.valueOf(IncomeTypeEnum.CLUB_INCOME), share, "Monthly club pool share");
        }
    }


    private boolean qualifiesForClub(User user) {
       // sample qualification: rank GOLD or above
        UserMap userMap = userMapRepository.findByUserNodeIdAndActiveStateCodeFkId(user.getNodeId(), "ACTIVE");
        if (userMap.getRank() == null) return false;
        return Arrays.asList("GOLD", "DIAMOND", "PLATINUM").contains(userMap.getRank().toUpperCase());
    }


    // Reward income: milestone-based  this is based on user
    public void evaluateRewards(User u) {
       // sample: team sales threshold
        BigDecimal teamSales = calculateTeamSales(u);
        BigDecimal threshold = BigDecimal.valueOf(100000);
        if (teamSales.compareTo(threshold) >= 0) {
           // get reward rule
            List<IncomeType> rules = incomeTypeRepository.findByIncomeTypeCodeAndActiveStateCodeFkId(IncomeTypeEnum.REWARD_INCOME,"ACTIVE");
            IncomeType rule = rules.stream().findFirst().orElse(null);
            if (rule != null) {
                BigDecimal reward = calculateByRule(rule, teamSales);
                saveLedger(u.getNodeId(), String.valueOf(IncomeTypeEnum.REWARD_INCOME), reward, "Team sales reward");
            }
        }
    }


    private BigDecimal calculateTeamSales(User u) {
        // naive calculation: sum of direct sponsor purchases - placeholder
        return BigDecimal.valueOf(120000); // pretend we computed this
    }


    // Fast track: one-time bonus for quick achievement   this is user-based
    public void evaluateFastTrack(User newUser, LocalDateTime joinDate, int directReferrals) {
        LocalDateTime cutoff = joinDate.plusDays(30);
        if (LocalDateTime.now().isBefore(cutoff) && directReferrals >= 5) {
            List<IncomeType> rules = incomeTypeRepository.findByIncomeTypeCodeAndActiveStateCodeFkId(IncomeTypeEnum.FAST_TRACK_BONUS,"ACTIVE");
            IncomeType rule = rules.stream().findFirst().orElse(null);
            if (rule != null) {
                BigDecimal bonus = rule.getPercentage(); // often fixed
                saveLedger(newUser.getNodeId(), String.valueOf(IncomeTypeEnum.FAST_TRACK_BONUS), bonus, "Fast track bonus");
            }
        }
    }


    // Mining profit sharing: proportional to investment  this is user-based
    public void distributeMiningProfit(BigDecimal totalProfit) {
        List<UserMap> userMaps = userMapRepository.findAll();
        BigDecimal totalInvestment = userMaps.stream()
                .map(UserMap::getMiningInvestment)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (totalInvestment.compareTo(BigDecimal.ZERO) == 0) return;
        for (UserMap u : userMaps) {
            BigDecimal share = u.getMiningInvestment().divide(totalInvestment, 8, BigDecimal.ROUND_HALF_UP)
                    .multiply(totalProfit).setScale(2, BigDecimal.ROUND_HALF_UP);
            saveLedger(u.getUserNodeId(), String.valueOf(IncomeTypeEnum.MINING_PROFIT_SHARING), share, "Mining profit share");
        }
    }

    // Mining generation: pay upline a percentage of downline mining profit
    public void distributeMiningGeneration(String minerId, BigDecimal minerProfit) {
        User miner = userRepository.findByNodeIdAndActiveStateCodeFkId(minerId,"ACTIVE");
       // assume we have rules per level
        List<IncomeType> rules = incomeTypeRepository.findByIncomeTypeCodeAndActiveStateCodeFkId(IncomeTypeEnum.MINING_GENERATION,"ACTIVE");
       // map level -> rule
        Map<Integer, IncomeType> levelRules = rules.stream().collect(Collectors.toMap(IncomeType::getLevel, r -> r));
        int level = 1;
        UserMap upline = userMapRepository.findByUserNodeIdAndActiveStateCodeFkId(miner.getParentNodeId(), "ACTIVE");
        while (upline != null && level <= 10) {
            IncomeType r = levelRules.get(level);
            if (r != null) {
                BigDecimal payout = calculateByRule(r, minerProfit);
                saveLedger(upline.getUserNodeId(), String.valueOf(IncomeTypeEnum.MINING_GENERATION), payout, "Mining gen from " + miner.getNodeId() + " level " + level);
            }
            String parentId = userRepository.fetchParentNodeBasedOnUserNodeId(upline.getUserNodeId(),"ACTIVE");
            upline = userMapRepository.findByUserNodeIdAndActiveStateCodeFkId(parentId, "ACTIVE");
            level++;
        }
    }


    // Node business sharing: based on nodeSharePercent  based on user
    public void distributeNodePool(BigDecimal totalNodePool) {
        List<UserMap> userMaps = userMapRepository.findAll();
        for (UserMap u : userMaps) {
            if (u.getNodeSharePercent().compareTo(BigDecimal.ZERO) <= 0) continue;
            BigDecimal share = totalNodePool.multiply(u.getNodeSharePercent()).divide(BigDecimal.valueOf(100));
            saveLedger(u.getUserNodeId(), String.valueOf(IncomeTypeEnum.NODE_BUSINESS_SHARING), share, "Node pool share");
        }
    }


    private BigDecimal calculateByRule(IncomeType rule, BigDecimal baseAmount) {
        if (rule == null || baseAmount == null) return BigDecimal.ZERO;
        if ("PERCENT".equalsIgnoreCase(rule.getRuleMode())) {
            return baseAmount.multiply(rule.getPercentage()).divide(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_HALF_UP);
        } else {
            // FIXED
            return rule.getPercentage().setScale(2, BigDecimal.ROUND_HALF_UP);
        }
    }


    private void saveLedger(String userId, String incomeType , BigDecimal amount, String notes) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) return;
        CommissionLedger ledger = new CommissionLedger();
        ledger.setUserNodeId(userId);
        ledger.setIncomeType(incomeType);
        ledger.setAmount(amount);
        ledger.setNote(notes);
        comissionLedgerRepository.save(ledger);
    }


    @Override
    public FinalResponse getIncomeSummary(Integer inputPkId, String inputFkId, int page, int size, String filterBy, String searchValue) {
        FinalResponse<IncomeSummary> finalResponse = new FinalResponse<>();
        Pageable pageable = Util.getPageable(size, page);
        List<IncomeSummary> incomeSummaryList = populateIncomeSummaryView(inputPkId,inputFkId, filterBy,searchValue, pageable);
        int count = populateIncomeSummaryCount(inputPkId, inputFkId, filterBy);
        finalResponse.setData(incomeSummaryList);
        finalResponse.setCount( count);
        Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }



    private int populateIncomeSummaryCount(Integer inputPkId, String inputFkId, String filterBy) {
        int count = 0;
        if(!Util.isDefined(filterBy)) {
            filterBy="ACTIVE";
        }
        if (Util.isDefined(inputPkId)) {
            count = incomeSummaryRepository.countByIncomeSummaryPkIdAndActiveStateCodeFkId(inputPkId, filterBy);
        }
        else if (Util.isDefined(inputFkId)) {
            count = incomeSummaryRepository.countByActiveStateCodeFkIdAndUserNodeId(filterBy,inputFkId);
        }
        else {
            count = incomeSummaryRepository.countByActiveStateCodeFkId(filterBy);
        }

        return count;
    }

    private List<IncomeSummary> populateIncomeSummaryView(Integer inputPkId, String inputFkId, String filterBy, String searchValue, Pageable pageable) {
        List<IncomeSummary> incomeSummaryList = new ArrayList<>();
        if (Util.isDefined(inputPkId)) {
            IncomeSummary incomeSummary = incomeSummaryRepository.findByIncomeSummaryPkIdAndActiveStateCodeFkId(inputPkId, filterBy);
            incomeSummaryList.add(incomeSummary);
        }
        else if(Util.isDefined(inputFkId)){
            incomeSummaryList = incomeSummaryRepository.findByActiveStateCodeFkIdAndUserNodeId(filterBy,inputFkId, pageable);
        }
        else {
            incomeSummaryList = incomeSummaryRepository.findByActiveStateCodeFkId(filterBy, pageable);
        }
        return incomeSummaryList;
    }

    @Override
    public FinalResponse addIncomeSummary(IncomeSummary incomeSummary) {
        FinalResponse finalResponse = new FinalResponse();
        String vLastModifiedDateTime = Util.getCurrentUTCTimestampString();
        incomeSummary.setEffectiveDateTime(vLastModifiedDateTime);
        //effective date cannot be greater than present date
        if (Util.compareDate(incomeSummary.getEffectiveDateTime(), vLastModifiedDateTime) > 0) {
            Util.setMessage(finalResponse, "100", "Error: Effective date time cannot be greater than the present moment.");
            return finalResponse;
        }

        Util.setCommonDefaultAttributes(incomeSummary);

        incomeSummaryRepository.save(incomeSummary);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return  finalResponse;
    }

    @Override
    public FinalResponse updateIncomeSummary(Integer id, IncomeSummary incomeSummary) {
        FinalResponse finalResponse = new FinalResponse();
        incomeSummaryRepository.findById(id)
                .map(existing -> {
                    existing.setUserNodeId(incomeSummary.getUserNodeId());
                    existing.setTransactionDate(incomeSummary.getTransactionDate());
                    existing.setTransactionType(incomeSummary.getTransactionType());
                    existing.setBonusAmount(incomeSummary.getBonusAmount());
                    return incomeSummaryRepository.save(existing);
                }).orElseThrow(() -> new RuntimeException(" IncomeSummary not found"));
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse deleteIncomeSummary(Integer id) {
        FinalResponse finalResponse = new FinalResponse();
        incomeSummaryRepository.deleteById(id);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse getAccountStatement(Integer inputPkId, String inputFkId, int page, int size, String filterBy, String searchValue) {
        FinalResponse<AccountStatement> finalResponse = new FinalResponse<>();
        Pageable pageable = Util.getPageable(size, page);
        List<AccountStatement> accountStatementList = populateAccountStatementView(inputPkId, inputFkId, filterBy, searchValue, pageable);
        int count = populateAccountStatementCount(inputPkId, inputFkId, filterBy);
        finalResponse.setData(accountStatementList);
        finalResponse.setCount(count);
        Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }


    private int populateAccountStatementCount(Integer inputPkId, String inputFkId, String filterBy) {
        int count = 0;
        if (!Util.isDefined(filterBy)) {
            filterBy = "ACTIVE";
        }
        if (Util.isDefined(inputPkId)) {
            count = accountStatementRepository.countByAccountStatementPkIdAndActiveStateCodeFkId(inputPkId, filterBy);
        } else if (Util.isDefined(inputFkId)) {
            count = accountStatementRepository.countByActiveStateCodeFkIdAndUserNodeId(filterBy, inputFkId);
        } else {
            count = accountStatementRepository.countByActiveStateCodeFkId(filterBy);
        }

        return count;
    }

    private List<AccountStatement> populateAccountStatementView(Integer inputPkId, String inputFkId, String filterBy, String searchValue, Pageable pageable) {
        List<AccountStatement> accountStatementList = new ArrayList<>();
        if (Util.isDefined(inputPkId)) {
            AccountStatement accountStatement = accountStatementRepository.findByAccountStatementPkIdAndActiveStateCodeFkId(inputPkId, filterBy);
            accountStatementList.add(accountStatement);
        } else if (Util.isDefined(inputFkId)) {
            accountStatementList = accountStatementRepository.findByActiveStateCodeFkIdAndUserNodeId(filterBy, inputFkId, pageable);
        } else {
            accountStatementList = accountStatementRepository.findByActiveStateCodeFkId(filterBy, pageable);
        }
        return accountStatementList;
    }

    @Override
    public FinalResponse addAccountStatement(AccountStatement accountStatement) {
        FinalResponse finalResponse = new FinalResponse();
        String vLastModifiedDateTime = Util.getCurrentUTCTimestampString();
        accountStatement.setEffectiveDateTime(vLastModifiedDateTime);
        //effective date cannot be greater than present date
        if (Util.compareDate(accountStatement.getEffectiveDateTime(), vLastModifiedDateTime) > 0) {
            Util.setMessage(finalResponse, "100", "Error: Effective date time cannot be greater than the present moment.");
            return finalResponse;
        }

        Util.setCommonDefaultAttributes(accountStatement);

        accountStatementRepository.save(accountStatement);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse updateAccountStatement(Integer id, AccountStatement accountStatement) {
        FinalResponse finalResponse = new FinalResponse();
        accountStatementRepository.findById(id)
                .map(existing -> {
                    existing.setUserNodeId(accountStatement.getUserNodeId());
                    existing.setCredit(accountStatement.getCredit());
                    existing.setDebit(accountStatement.getDebit());
                    existing.setParticular(accountStatement.getParticular());
                    return accountStatementRepository.save(existing);
                }).orElseThrow(() -> new RuntimeException(" AccountStatement not found"));
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse deleteAccountStatement(Integer id) {
        FinalResponse finalResponse = new FinalResponse();
        accountStatementRepository.deleteById(id);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse getBussinessHistory(Integer inputPkId, String inputFkId, int page, int size, String filterBy, String searchValue) {
        FinalResponse<BusinessHistory> finalResponse = new FinalResponse<>();
        Pageable pageable = Util.getPageable(size, page);
        List<BusinessHistory> businessHistoryList = populateBusinessHistoryView(inputPkId, inputFkId, filterBy, searchValue, pageable);
        int count = populateBusinessHistoryCount(inputPkId, inputFkId, filterBy);
        finalResponse.setData(businessHistoryList);
        finalResponse.setCount(count);
        Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }


    private int populateBusinessHistoryCount(Integer inputPkId, String inputFkId, String filterBy) {
        int count = 0;
        if (!Util.isDefined(filterBy)) {
            filterBy = "ACTIVE";
        }
        if (Util.isDefined(inputPkId)) {
            count = businessHistoryRepository.countByBusinessHistoryPkIdAndActiveStateCodeFkId(inputPkId, filterBy);
        } else if (Util.isDefined(inputFkId)) {
            count = businessHistoryRepository.countByActiveStateCodeFkIdAndUserNodeId(filterBy, inputFkId);
        } else {
            count = businessHistoryRepository.countByActiveStateCodeFkId(filterBy);
        }

        return count;
    }

    private List<BusinessHistory> populateBusinessHistoryView(Integer inputPkId, String inputFkId, String filterBy, String searchValue, Pageable pageable) {
        List<BusinessHistory> businessHistoryList = new ArrayList<>();
        if (Util.isDefined(inputPkId)) {
            BusinessHistory businessHistory = businessHistoryRepository.findByBusinessHistoryPkIdAndActiveStateCodeFkId(inputPkId, filterBy);
            businessHistoryList.add(businessHistory);
        } else if (Util.isDefined(inputFkId)) {
            businessHistoryList = businessHistoryRepository.findByActiveStateCodeFkIdAndUserNodeId(filterBy, inputFkId, pageable);
        } else {
            businessHistoryList = businessHistoryRepository.findByActiveStateCodeFkId(filterBy, pageable);
        }
        return businessHistoryList;
    }


    @Override
    public FinalResponse addBusinessHistory(BusinessHistory businessHistory) {
        FinalResponse finalResponse = new FinalResponse();
        String vLastModifiedDateTime = Util.getCurrentUTCTimestampString();
        businessHistory.setEffectiveDateTime(vLastModifiedDateTime);
        //effective date cannot be greater than present date
        if (Util.compareDate(businessHistory.getEffectiveDateTime(), vLastModifiedDateTime) > 0) {
            Util.setMessage(finalResponse, "100", "Error: Effective date time cannot be greater than the present moment.");
            return finalResponse;
        }

        Util.setCommonDefaultAttributes(businessHistory);

        businessHistoryRepository.save(businessHistory);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse updateBusinessHistory(Integer id, BusinessHistory businessHistory) {
        FinalResponse finalResponse = new FinalResponse();
        businessHistoryRepository.findById(id)
                .map(existing -> {
                    existing.setUserNodeId(businessHistory.getUserNodeId());
                    existing.setUserName(businessHistory.getUserName());
                    existing.setParentNodeId(businessHistory.getParentNodeId());
                    existing.setAmount(businessHistory.getAmount());
                    existing.setType(businessHistory.getType());
                    existing.setMode(businessHistory.getMode());
                    return businessHistoryRepository.save(existing);
                }).orElseThrow(() -> new RuntimeException(" businessHistory not found"));
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse deleteBusinessHistory(Integer id) {
        FinalResponse finalResponse = new FinalResponse();
        businessHistoryRepository.deleteById(id);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse getIndividualRankReward(Integer inputPkId, String inputFkId, int page, int size, String filterBy, String searchValue) {
        FinalResponse<IndividualRankReward> finalResponse = new FinalResponse<>();
        Pageable pageable = Util.getPageable(size, page);
        List<IndividualRankReward> individualRankRewardList = populateIndividualRankRewardView(inputPkId, inputFkId, filterBy, searchValue, pageable);
        int count = populateIndividualRankRewardCount(inputPkId, inputFkId, filterBy);
        finalResponse.setData(individualRankRewardList);
        finalResponse.setCount(count);
        Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }


    private int populateIndividualRankRewardCount(Integer inputPkId, String inputFkId, String filterBy) {
        int count = 0;
        if (!Util.isDefined(filterBy)) {
            filterBy = "ACTIVE";
        }
        if (Util.isDefined(inputPkId)) {
            count = individualRankRewardRepository.countByIndividualRankPkIdAndActiveStateCodeFkId(inputPkId, filterBy);
        } else if (Util.isDefined(inputFkId)) {
            count = individualRankRewardRepository.countByActiveStateCodeFkIdAndUserNodeId(filterBy, inputFkId);
        } else {
            count = individualRankRewardRepository.countByActiveStateCodeFkId(filterBy);
        }

        return count;
    }

    private List<IndividualRankReward> populateIndividualRankRewardView(Integer inputPkId, String inputFkId, String filterBy, String searchValue, Pageable pageable) {
        List<IndividualRankReward> individualRankRewardList = new ArrayList<>();
        if (Util.isDefined(inputPkId)) {
            IndividualRankReward individualRankReward = individualRankRewardRepository.findByIndividualRankPkIdAndActiveStateCodeFkId(inputPkId, filterBy);
            individualRankRewardList.add(individualRankReward);
        } else if (Util.isDefined(inputFkId)) {
            individualRankRewardList = individualRankRewardRepository.findByActiveStateCodeFkIdAndUserNodeId(filterBy, inputFkId, pageable);
        } else {
            individualRankRewardList = individualRankRewardRepository.findByActiveStateCodeFkId(filterBy, pageable);
        }
        return individualRankRewardList;
    }

    @Override
    public FinalResponse addIndividualRankReward(IndividualRankReward individualRankReward) {
        FinalResponse finalResponse = new FinalResponse();
        String vLastModifiedDateTime = Util.getCurrentUTCTimestampString();
        individualRankReward.setEffectiveDateTime(vLastModifiedDateTime);
        //effective date cannot be greater than present date
        if (Util.compareDate(individualRankReward.getEffectiveDateTime(), vLastModifiedDateTime) > 0) {
            Util.setMessage(finalResponse, "100", "Error: Effective date time cannot be greater than the present moment.");
            return finalResponse;
        }

        Util.setCommonDefaultAttributes(individualRankReward);

        individualRankRewardRepository.save(individualRankReward);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse updateIndividualRankReward(Integer id, IndividualRankReward individualRankReward) {
        FinalResponse finalResponse = new FinalResponse();
        individualRankRewardRepository.findById(id)
                .map(existing -> {
                    existing.setUserNodeId(individualRankReward.getUserNodeId());
                    existing.setRankName(individualRankReward.getRankName());
                    existing.setRankCodeFkId(individualRankReward.getRankCodeFkId());
                    existing.setMatching(individualRankReward.getMatching());
                    existing.setReward(individualRankReward.getReward());
                    existing.setAchieved(individualRankReward.getAchieved());
                    return individualRankRewardRepository.save(existing);
                }).orElseThrow(() -> new RuntimeException(" individualRankReward not found"));
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse deleteIndividualRankReward(Integer id) {
        FinalResponse finalResponse = new FinalResponse();
        individualRankRewardRepository.deleteById(id);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse getCryptoDepositSummary(String inputPkId, String inputFkId, int page, int size, String filterBy, String searchValue) {
        FinalResponse<CryptoDeposit> finalResponse = new FinalResponse<>();
        Pageable pageable = Util.getPageable(size, page);
        List<CryptoDeposit> cryptoDepositList = populateCryptoDepositView(inputPkId, inputFkId, filterBy, searchValue, pageable);
        int count = populateCryptoDepositCount(inputPkId, inputFkId, filterBy);
        finalResponse.setData(cryptoDepositList);
        finalResponse.setCount(count);
        Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }


    private int populateCryptoDepositCount(String inputPkId, String inputFkId, String filterBy) {
        int count = 0;
        if (!Util.isDefined(filterBy)) {
            filterBy = "ACTIVE";
        }
        if (Util.isDefined(inputPkId)) {
//            count = cryptoDepositRepository.countByIndividualRankPkIdAndActiveStateCodeFkId(inputPkId, filterBy);
        } else if (Util.isDefined(inputFkId)) {
            count = cryptoDepositRepository.countByActiveStateCodeFkIdAndUserNodeId(filterBy, inputFkId);
        } else {
            count = cryptoDepositRepository.countByActiveStateCodeFkId(filterBy);
        }

        return count;
    }

    private List<CryptoDeposit> populateCryptoDepositView(String inputPkId, String inputFkId, String filterBy, String searchValue, Pageable pageable) {
        List<CryptoDeposit> cryptoDepositList = new ArrayList<>();
        if (Util.isDefined(inputPkId)) {
//            CryptoDeposit cryptoDeposit = cryptoDepositRepository.findByIndividualRankPkIdAndActiveStateCodeFkId(inputPkId, filterBy);
//            cryptoDepositList.add(cryptoDeposit);
        } else if (Util.isDefined(inputFkId)) {
            cryptoDepositList = cryptoDepositRepository.findByActiveStateCodeFkIdAndUserNodeId(filterBy, inputFkId, pageable);
        } else {
            cryptoDepositList = cryptoDepositRepository.findByActiveStateCodeFkId(filterBy, pageable);
        }
        return cryptoDepositList;
    }

    @Override
    public FinalResponse addCryptoDeposit(CryptoDeposit cryptoDeposit) {
        FinalResponse finalResponse = new FinalResponse();
        String vLastModifiedDateTime = Util.getCurrentUTCTimestampString();
        cryptoDeposit.setEffectiveDateTime(vLastModifiedDateTime);
        //effective date cannot be greater than present date
        if (Util.compareDate(cryptoDeposit.getEffectiveDateTime(), vLastModifiedDateTime) > 0) {
            Util.setMessage(finalResponse, "100", "Error: Effective date time cannot be greater than the present moment.");
            return finalResponse;
        }

        Util.setCommonDefaultAttributes(cryptoDeposit);

        cryptoDepositRepository.save(cryptoDeposit);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse updateCryptoDeposit(Integer id, CryptoDeposit cryptoDeposit) {
        FinalResponse finalResponse = new FinalResponse();
        cryptoDepositRepository.findById(id)
                .map(existing -> {
                    existing.setUserNodeId(cryptoDeposit.getUserNodeId());
                    existing.setAmount(cryptoDeposit.getAmount());
                    existing.setCurrency(cryptoDeposit.getCurrency());
                    existing.setPayAddress(cryptoDeposit.getPayAddress());
                    existing.setPayInaddress(cryptoDeposit.getPayInaddress());
                    existing.setPayoutAddress(cryptoDeposit.getPayoutAddress());
                    existing.setPaymentId(cryptoDeposit.getPaymentId());
                    existing.setPaymentStatus(cryptoDeposit.getPaymentStatus());
                    return cryptoDepositRepository.save(existing);
                }).orElseThrow(() -> new RuntimeException(" CryptoDeposit not found"));
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse deleteCryptoDeposit(Integer id) {
        FinalResponse finalResponse = new FinalResponse();
        cryptoDepositRepository.deleteById(id);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }


}
