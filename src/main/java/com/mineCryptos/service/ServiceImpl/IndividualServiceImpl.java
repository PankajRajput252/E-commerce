package com.mineCryptos.service.ServiceImpl;

import com.mineCryptos.model.FinalResponse;
import com.mineCryptos.model.User;
import com.mineCryptos.model.Util;
import com.mineCryptos.model.entitities.admin.RankReward;
import com.mineCryptos.model.entitities.enduser.*;
import com.mineCryptos.repo.UserRepository;
import com.mineCryptos.repo.enduser.*;
import com.mineCryptos.service.Service.IndividualService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        if (Util.isDefined(inputPkId)) {
            count = walletRepository.countByWalletPkIdAndActiveStateCodeFkId(inputPkId, "ACTIVE");
        }
        else if (Util.isDefined(inputFkId)) {
            count = walletRepository.countByActiveStateCodeFkIdAndUserNodeCode("ACTIVE", inputFkId);
        }
        else {
            count = walletRepository.countByActiveStateCodeFkId("ACTIVE");
        }

        return count;
    }

    private List<Wallet> populateWalletView(Integer inputPkId, String inputFkId, String filterBy, String searchValue, Pageable pageable) {
        List<Wallet> walletList = new ArrayList<>();
        if (Util.isDefined(inputPkId)) {
            Wallet wallet = walletRepository.findByWalletPkIdAndActiveStateCodeFkId(inputPkId, "ACTIVE");
            walletList.add(wallet);
        }
        else if (Util.isDefined(inputFkId)) {
            walletList = walletRepository.findByActiveStateCodeFkIdAndUserNodeCode("ACTIVE", inputFkId,pageable);
        }
        else {
            walletList = walletRepository.findByActiveStateCodeFkId("ACTIVE", pageable);
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
        if (Util.isDefined(inputPkId)) {
            count = individualIncomeSummaryRepository.countByIndividualIncomeSummaryPkIdAndActiveStateCodeFkId(inputPkId, "ACTIVE");
        }
        else if(Util.isDefined(inputFkId)){
            count = individualIncomeSummaryRepository.countByActiveStateCodeFkIdAndUserNodeId("ACTIVE",inputFkId);
        }
        else {
            count = individualIncomeSummaryRepository.countByActiveStateCodeFkId("ACTIVE");
        }

        return count;
    }

    private List<IndividualIncomeSummary> populateIndividualIncomeSummaryView(Integer inputPkId, String inputFkId, String filterBy, String searchValue, Pageable pageable) {
        List<IndividualIncomeSummary> individualIncomeSummaryList = new ArrayList<>();
        if (Util.isDefined(inputPkId)) {
            IndividualIncomeSummary individualIncomeSummary = individualIncomeSummaryRepository.findByIndividualIncomeSummaryPkIdAndActiveStateCodeFkId(inputPkId, "ACTIVE");
            individualIncomeSummaryList.add(individualIncomeSummary);
        }
        else if(Util.isDefined(inputFkId)){
            individualIncomeSummaryList = individualIncomeSummaryRepository.findByActiveStateCodeFkIdAndUserNodeId("ACTIVE",inputFkId, pageable);
        }
        else {
            individualIncomeSummaryList = individualIncomeSummaryRepository.findByActiveStateCodeFkId("ACTIVE", pageable);
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
    public FinalResponse getIndividualMiningPackage(Integer inputPkId, String inputFkId, int page, int size, String filterBy, String searchValue){
        FinalResponse<MiningPackage> finalResponse = new FinalResponse<>();
        Pageable pageable = Util.getPageable(size, page);
        List<MiningPackage> miningPackageList = populateMiningPackageView(inputPkId,inputFkId, filterBy,searchValue, pageable);
        int count = populateMiningPackageCount(inputPkId, inputFkId, filterBy);
        finalResponse.setData(miningPackageList);
        finalResponse.setCount( count);
        Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    private int populateMiningPackageCount(Integer inputPkId, String inputFkId, String filterBy) {
        int count = 0;
        if (Util.isDefined(inputPkId)) {
            count = miningPackageRepository.countByMiningPackagePkIdAndActiveStateCodeFkId(inputPkId, "ACTIVE");
        }
        else if(Util.isDefined(inputFkId)) {
            count = miningPackageRepository.countByActiveStateCodeFkIdAndUserNodeCode("ACTIVE",inputFkId);
        }
        else {
            count = miningPackageRepository.countByActiveStateCodeFkId("ACTIVE");
        }

        return count;
    }

    private List<MiningPackage> populateMiningPackageView(Integer inputPkId, String inputFkId, String filterBy, String searchValue, Pageable pageable) {
        List<MiningPackage> miningPackageList = new ArrayList<>();
        if (Util.isDefined(inputPkId)) {
            MiningPackage miningPackage = miningPackageRepository.findByMiningPackagePkIdAndActiveStateCodeFkId(inputPkId, "ACTIVE");
            miningPackageList.add(miningPackage);
        }
        else if(Util.isDefined(inputFkId)) {
            miningPackageList = miningPackageRepository.findByActiveStateCodeFkIdAndUserNodeCode("ACTIVE",inputFkId, pageable);
        }
        else {
            miningPackageList = miningPackageRepository.findByActiveStateCodeFkId("ACTIVE", pageable);
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
        if (Util.isDefined(inputPkId)) {
            count = depositFundRepository.countByDepositPkIdAndActiveStateCodeFkId(inputPkId, "ACTIVE");
        }
        else if (Util.isDefined(inputFkId)) {
            count = depositFundRepository.countByActiveStateCodeFkIdAndUserNodeCode("ACTIVE",inputFkId);
        }
        else {
            count = depositFundRepository.countByActiveStateCodeFkId("ACTIVE");
        }

        return count;
    }

    private List<DepositFund> populateDepositFundView(Integer inputPkId, String inputFkId, String filterBy, String searchValue, Pageable pageable) {
        List<DepositFund> depositFundList = new ArrayList<>();
        if (Util.isDefined(inputPkId)) {
            DepositFund depositFund = depositFundRepository.findByDepositPkIdAndActiveStateCodeFkId(inputPkId, "ACTIVE");
            depositFundList.add(depositFund);
        }
        else if (Util.isDefined(inputFkId)) {
            depositFundList = depositFundRepository.findByActiveStateCodeFkIdAndUserNodeCode("ACTIVE",inputFkId, pageable);
        }
        else {
            depositFundList = depositFundRepository.findByActiveStateCodeFkId("ACTIVE", pageable);
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
        if (Util.isDefined(inputPkId)) {
            count = walletTransactionRepository.countByWalletTxnPkIdAndActiveStateCodeFkId(inputPkId, "ACTIVE");
        }
        else if (Util.isDefined(inputFkId)) {
            count = walletTransactionRepository.countByActiveStateCodeFkIdAndFromUserId("ACTIVE",inputFkId);
        }
        else {
            count = walletTransactionRepository.countByActiveStateCodeFkId("ACTIVE");
        }

        return count;
    }

    private List<WalletTransaction> populateWalletTransactionView(Integer inputPkId, String inputFkId, String filterBy, String searchValue, Pageable pageable) {
        List<WalletTransaction> walletTransactionList = new ArrayList<>();
        if (Util.isDefined(inputPkId)) {
            WalletTransaction walletTransaction = walletTransactionRepository.findByWalletTxnPkIdAndActiveStateCodeFkId(inputPkId, "ACTIVE");
            walletTransactionList.add(walletTransaction);
        }
        else if (Util.isDefined(inputFkId)) {
            walletTransactionList = walletTransactionRepository.findByActiveStateCodeFkIdAndFromUserId("ACTIVE",inputFkId, pageable);
        }
        else {
            walletTransactionList = walletTransactionRepository.findByActiveStateCodeFkId("ACTIVE", pageable);
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
        if (Util.isDefined(inputPkId)) {
            count = userRepository.countByParentNodeIdAndActiveStateCodeFkId(inputPkId, "ACTIVE");
        }
        else if(Util.isDefined(searchValue)){
            count=userRepository.countByActiveStateCodeFkIdAndParentNodeIdContaining("ACTIVE",searchValue);
        }
        else {
            count = userRepository.countByActiveStateCodeFkId("ACTIVE");
        }
        return count;
    }

    private List<MemberDetail> populateMemberDetailView(String inputPkId, String inputFkId, String filterBy, String searchValue, Pageable pageable) {
        List<MemberDetail> memberDetailList = new ArrayList<>();
        List<User> userList=new ArrayList<>();
        if (Util.isDefined(inputPkId)) {
             userList = userRepository.findByParentNodeIdAndActiveStateCodeFkId(inputPkId, "ACTIVE",pageable);
        }
        else if(Util.isDefined(searchValue)){
            userList=userRepository.findByActiveStateCodeFkIdAndParentNodeIdContaining("ACTIVE",searchValue,pageable);
        }
        else {
            userList = userRepository.findByActiveStateCodeFkId("ACTIVE", pageable);
        }
        if(Util.isDefined(userList)){
            AtomicInteger levelCount = new AtomicInteger(0);
            userList.stream().map((user)->{
                MemberDetail memberDetail = new MemberDetail();
                memberDetail.setMemberId(user.getNodeId());
                memberDetail.setMemberLevel(levelCount.getAndIncrement()); // increments each time
                memberDetail.setMemberName(user.getName());
                memberDetail.setMemberEmail(user.getEmail());
                memberDetailList.add(memberDetail);
                return user;
            }).collect(Collectors.toList());
        }
        return memberDetailList;
    }

    @Override
    public  FinalResponse getTeamHierarchy(String inputPkId, String inputFkId, int page, int size, String filterBy, String searchValue){
        FinalResponse<MemberDetail> finalResponse = new FinalResponse<>();
        Pageable pageable = Util.getPageable(size, page);
        List<MemberDetail> team=new ArrayList<>();
        fetchTeamRecursive(inputPkId, 0, team);
        finalResponse.setData(team);
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
            detail.setPosition(child.getPosition());
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
        if (Util.isDefined(inputPkId)) {
            count = walletTransactionRepository.countByWalletTxnPkIdAndActiveStateCodeFkId(inputPkId, "ACTIVE");
        }
        else if(Util.isDefined(inputFkId)) {
            count = supportTicketRepository.countByActiveStateCodeFkIdAndUserNodeId("ACTIVE", inputFkId);
        }
        else {
            count = walletTransactionRepository.countByActiveStateCodeFkId("ACTIVE");
        }

        return count;
    }

    private List<SupportTicket> populateSupportTicketView(Integer inputPkId, String inputFkId, String filterBy, String searchValue, Pageable pageable) {
        List<SupportTicket> supportTicketList = new ArrayList<>();
        if (Util.isDefined(inputPkId)) {
            SupportTicket supportTicket = supportTicketRepository.findBySupportTicketPkIdAndActiveStateCodeFkId(inputPkId, "ACTIVE");
            supportTicketList.add(supportTicket);
        }
        else if(Util.isDefined(inputFkId)) {
            supportTicketList = supportTicketRepository.findByActiveStateCodeFkIdAndUserNodeId("ACTIVE", inputFkId,pageable);
        }
        else {
            supportTicketList = supportTicketRepository.findByActiveStateCodeFkId("ACTIVE", pageable);
        }
        supportTicketList.stream().map((supportTicket)->{
            String userName= userRepository.fetchUserNameBasedOnNodeId(supportTicket.getUserNodeId(),"ACTIVE");
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
        int count = 0;
        if (Util.isDefined(inputPkId)) {
            count = withdrawalRequestRepository.countByWithdrawalRequestPkIdAndActiveStateCodeFkId(inputPkId, "ACTIVE");
        }
        else if (Util.isDefined(inputFkId)) {
            count = withdrawalRequestRepository.countByActiveStateCodeFkIdAndUserNodeId("ACTIVE",inputFkId);
        }
        else {
            count = withdrawalRequestRepository.countByActiveStateCodeFkId("ACTIVE");
        }

        return count;
    }

    private List<WithdrawalRequest> populateWithdrawalRequestView(Integer inputPkId, String inputFkId, String filterBy, String searchValue, Pageable pageable) {
        List<WithdrawalRequest> withdrawalRequestList = new ArrayList<>();
        if (Util.isDefined(inputPkId)) {
            WithdrawalRequest withdrawalRequest = withdrawalRequestRepository.findByWithdrawalRequestPkIdAndActiveStateCodeFkId(inputPkId, "ACTIVE");
            withdrawalRequestList.add(withdrawalRequest);
        }
        else if(Util.isDefined(inputFkId)){
            withdrawalRequestList = withdrawalRequestRepository.findByActiveStateCodeFkIdAndUserNodeId("ACTIVE",inputFkId, pageable);
        }
        else {
            withdrawalRequestList = withdrawalRequestRepository.findByActiveStateCodeFkId("ACTIVE", pageable);
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

}
