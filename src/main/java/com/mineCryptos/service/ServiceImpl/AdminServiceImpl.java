package com.mineCryptos.service.ServiceImpl;

import com.mineCryptos.FinalException;
import com.mineCryptos.model.FinalResponse;
import com.mineCryptos.model.User;
import com.mineCryptos.model.Util;
import com.mineCryptos.model.entitities.admin.AdminDashboardInfo;
import com.mineCryptos.model.entitities.admin.IncomeType;
import com.mineCryptos.model.entitities.admin.RankReward;
import com.mineCryptos.model.entitities.admin.SubscriptionDefinition;
import com.mineCryptos.model.entitities.enduser.AccountStatement;
import com.mineCryptos.model.entitities.enduser.DepositFund;
import com.mineCryptos.model.entitities.enduser.Wallet;
import com.mineCryptos.model.entitities.enduser.WalletTransaction;
import com.mineCryptos.repo.UserRepository;
import com.mineCryptos.repo.admin.IncomeTypeRepository;
import com.mineCryptos.repo.admin.RankRewardRepository;
import com.mineCryptos.repo.admin.SubscriptionDefinitionRepo;
import com.mineCryptos.repo.enduser.DepositFundRepository;
import com.mineCryptos.repo.enduser.WalletRepository;
import com.mineCryptos.repo.enduser.WalletTransactionRepository;
import com.mineCryptos.service.Service.AdminService;
import com.mineCryptos.service.Service.IndividualService;
import net.bytebuddy.dynamic.DynamicType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private RankRewardRepository rankRewardRepository;
    @Autowired
    private IncomeTypeRepository incomeTypeRepository;
    @Autowired
    private DepositFundRepository depositFundRepository;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private WalletTransactionRepository walletTransactionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private IndividualService individualService;
    @Autowired
    private SubscriptionDefinitionRepo subscriptionDefinitionRepo;

    @Override
    @Transactional(readOnly = true)
    public FinalResponse getRankAndReward(Integer inputPkId, Integer inputFkId, int page, int size, String filterBy, String searchValue) throws FinalException {
        FinalResponse<RankReward> finalResponse = new FinalResponse<>();
        Pageable pageable = Util.getPageable(size, page);
        List<RankReward> rankRewardList = populateRankRewardView(inputPkId,inputFkId, filterBy,searchValue, pageable);
        int count = populateRankRewardViewCount(inputPkId, inputFkId, filterBy);
        finalResponse.setData(rankRewardList);
        finalResponse.setCount( count);
        Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    private int populateRankRewardViewCount(Integer inputPkId, Integer inputFkId, String filterBy) {
        int count = 0;
        if (Util.isDefined(inputPkId)) {
            count = rankRewardRepository.countByRankIdAndActiveStateCodeFkId(inputPkId, "ACTIVE");
        }  else {
            count = rankRewardRepository.countByActiveStateCodeFkId("ACTIVE");
        }

        return count;
    }

    private List<RankReward> populateRankRewardView(Integer inputPkId, Integer inputFkId, String filterBy, String searchValue, Pageable pageable) {
        List<RankReward> rankRewardList = new ArrayList<>();
        if (Util.isDefined(inputPkId)) {
            RankReward rankReward = rankRewardRepository.findByRankIdAndActiveStateCodeFkId(inputPkId, "ACTIVE");
            rankRewardList.add(rankReward);
        } else {
            rankRewardList = rankRewardRepository.findByActiveStateCodeFkId("ACTIVE", pageable);
        }
        return rankRewardList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinalResponse addRankAndReward(RankReward rankReward) throws FinalException {
        FinalResponse finalResponse = new FinalResponse();
        String vLastModifiedDateTime = Util.getCurrentUTCTimestampString();
        rankReward.setEffectiveDateTime(vLastModifiedDateTime);
        //effective date cannot be greater than present date
        if (Util.compareDate(rankReward.getEffectiveDateTime(), vLastModifiedDateTime) > 0) {
            Util.setMessage(finalResponse, "100", "Error: Effective date time cannot be greater than the present moment.");
            return finalResponse;
        }

        int count = rankRewardRepository.countByRankNameAndActiveStateCodeFkId(rankReward.getRankName(), "ACTIVE");
        if (count > 0) {
            Util.setMessage(finalResponse, "100", "Error: This Rank and reward already exists.");
            return finalResponse;
        }
        rankReward.setRankCode(rankReward.getRankName().toUpperCase());
        Util.setCommonDefaultAttributes(rankReward);

        rankRewardRepository.save(rankReward);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinalResponse updateRankAndReward(Integer rankId, RankReward updatedRank) {
        FinalResponse finalResponse = new FinalResponse();
         rankRewardRepository.findById(rankId)
                .map(existing -> {
                    existing.setRankName(updatedRank.getRankName());
                    existing.setMatching(updatedRank.getMatching());
                    existing.setReward(updatedRank.getReward());
                    existing.setAchieved(updatedRank.isAchieved());
                    return rankRewardRepository.save(existing);
                }).orElseThrow(() -> new RuntimeException("Rank not found"));
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
   public FinalResponse deleteRankAndReward(Integer id){
        FinalResponse finalResponse = new FinalResponse();
        rankRewardRepository.deleteById(id);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }


    @Override
    @Transactional(readOnly = true)
    public FinalResponse getIncomeType(Integer inputPkId, Integer inputFkId, int page, int size, String filterBy, String searchValue) throws FinalException {
        FinalResponse<IncomeType> finalResponse = new FinalResponse<>();
        Pageable pageable = Util.getPageable(size, page);
        List<IncomeType> incomeTypeList = populateIncomeTypeView(inputPkId,inputFkId, filterBy,searchValue, pageable);
        int count = populateIncomeTypeCount(inputPkId, inputFkId, filterBy);
        finalResponse.setData(incomeTypeList);
        finalResponse.setCount( count);
        Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    private int populateIncomeTypeCount(Integer inputPkId, Integer inputFkId, String filterBy) {
        int count = 0;
        if (Util.isDefined(inputPkId)) {
            count = incomeTypeRepository.countByIncomeTypePkIdAndActiveStateCodeFkId(inputPkId, "ACTIVE");
        }  else {
            count = incomeTypeRepository.countByActiveStateCodeFkId("ACTIVE");
        }

        return count;
    }

    private List<IncomeType> populateIncomeTypeView(Integer inputPkId, Integer inputFkId, String filterBy, String searchValue, Pageable pageable) {
        List<IncomeType> incomeTypeList = new ArrayList<>();
        if (Util.isDefined(inputPkId)) {
            IncomeType incomeType = incomeTypeRepository.findByIncomeTypePkIdAndActiveStateCodeFkId(inputPkId, "ACTIVE");
            incomeTypeList.add(incomeType);
        } else {
            incomeTypeList = incomeTypeRepository.findByActiveStateCodeFkId("ACTIVE", pageable);
        }
        return incomeTypeList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinalResponse addIncomeType(IncomeType incomeType) throws FinalException {
        FinalResponse finalResponse = new FinalResponse();
        String vLastModifiedDateTime = Util.getCurrentUTCTimestampString();
        incomeType.setEffectiveDateTime(vLastModifiedDateTime);
        //effective date cannot be greater than present date
        if (Util.compareDate(incomeType.getEffectiveDateTime(), vLastModifiedDateTime) > 0) {
            Util.setMessage(finalResponse, "100", "Error: Effective date time cannot be greater than the present moment.");
            return finalResponse;
        }

        Util.setCommonDefaultAttributes(incomeType);

        incomeTypeRepository.save(incomeType);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinalResponse updateIncomeType(Integer rankId, IncomeType incomeType) {
        FinalResponse finalResponse = new FinalResponse();
        incomeTypeRepository.findById(rankId)
                .map(existing -> {
                    existing.setIncomeName(incomeType.getIncomeName());
                    existing.setIncomeTypeCode(incomeType.getIncomeTypeCode());
                    existing.setPercentage(incomeType.getPercentage());
                    existing.setLevel(incomeType.getLevel());
                    return incomeTypeRepository.save(existing);
                }).orElseThrow(() -> new RuntimeException("IncomeType not found"));
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinalResponse deleteIncomeType(Integer id){
        FinalResponse finalResponse = new FinalResponse();
        incomeTypeRepository.deleteById(id);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinalResponse confirmDeposit(String paymentId) {
        FinalResponse finalResponse = new FinalResponse();
        DepositFund depositFund = depositFundRepository.findByPaymentIdAndActiveStateCodeFkId(paymentId, "ACTIVE");
        if (Util.isDefined(depositFund)) {
            Optional<DepositFund> depositFunds = Optional.ofNullable(depositFund);
            depositFunds.map(existing -> {
                existing.setConfirmedAt(LocalDateTime.now());
                existing.setStatus("SUCCESS");
                return depositFundRepository.save(existing);
            });
            Wallet wallet=walletRepository.findByActiveStateCodeFkIdAndUserNodeCode("ACTIVE",depositFund.getUserNodeCode());
            if (Util.isDefined(wallet)) {
                double currentCapitalAmount=walletRepository.fetchUserCapitalWalletAmount(depositFund.getUserNodeCode(),"ACTIVE");
                double currentNodeAmount=walletRepository.fetchUserNodeWalletAmount(depositFund.getUserNodeCode(),"ACTIVE");
                double halfAmountToBeAdded=depositFund.getAmount()/2;
                double totalCapitalAmount=currentCapitalAmount+halfAmountToBeAdded;
                double totalNodeAmount=currentNodeAmount+halfAmountToBeAdded;
                walletRepository.updateCapitalWalletOfUser(totalCapitalAmount,depositFund.getUserNodeCode());
                walletRepository.updateNodeWalletOfUser(totalNodeAmount,depositFund.getUserNodeCode());
            }
            else{
                 wallet = new Wallet();
                wallet.setUserNodeCode(depositFund.getUserNodeCode());
                finalResponse = individualService.addWalletData(wallet);
                if (!finalResponse.getStatusCode().equals("200")) {
                    return finalResponse;
                }
                double currentCapitalAmount=walletRepository.fetchUserCapitalWalletAmount(depositFund.getUserNodeCode(),"ACTIVE");
                double currentNodeAmount=walletRepository.fetchUserNodeWalletAmount(depositFund.getUserNodeCode(),"ACTIVE");
                double halfAmountToBeAdded=depositFund.getAmount()/2;
                double totalCapitalAmount=currentCapitalAmount+halfAmountToBeAdded;
                double totalNodeAmount=currentNodeAmount+halfAmountToBeAdded;
                walletRepository.updateCapitalWalletOfUser(totalCapitalAmount,depositFund.getUserNodeCode());
                walletRepository.updateNodeWalletOfUser(totalNodeAmount,depositFund.getUserNodeCode());

                finalResponse= confirmUser(depositFund.getUserNodeCode());
                if (!finalResponse.getStatusCode().equals("200")) {
                    return finalResponse;
                }
            }
            AccountStatement accountStatement=new AccountStatement();
            accountStatement.setCredit(depositFund.getAmount());
            accountStatement.setUserNodeId(depositFund.getUserNodeCode());
            accountStatement.setParticular("This is amount "+ depositFund.getAmount()+"is credited during deposit.");
           individualService.addAccountStatement(accountStatement);
        } else {
            Util.setMessage(finalResponse, "100", "Error:Deposit not found.");
            return finalResponse;
        }
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinalResponse confirmWalletTransaction(Integer walletTxnPkId) {
        FinalResponse finalResponse = new FinalResponse();
        WalletTransaction walletTransaction = walletTransactionRepository.findByWalletTxnPkIdAndActiveStateCodeFkId(walletTxnPkId, "ACTIVE");
        if (Util.isDefined(walletTransaction)) {
            Optional<WalletTransaction> depositFunds = Optional.ofNullable(walletTransaction);
            depositFunds.map(existing -> {
                existing.setConfirmedAt(LocalDateTime.now());
                existing.setStatus("SUCCESS");
                return walletTransactionRepository.save(existing);
            });

            if (walletTransaction.getToWallet().equalsIgnoreCase("CAPITAL_WALLET")) {
                //deducting amount fromUser
                double currentFromWalletAmount = walletRepository.fetchUserCapitalWalletAmount(walletTransaction.getFromUserId(), "ACTIVE");
                double totalAmountLeftInFromWallet = currentFromWalletAmount - walletTransaction.getAmount();
                if (totalAmountLeftInFromWallet < 0) {
                    Util.setMessage(finalResponse, "100", "Error: You do not have enough Capital Amount to transfer.");
                    return finalResponse;
                }
                walletRepository.updateCapitalWalletOfUser(totalAmountLeftInFromWallet, walletTransaction.getFromUserId());

                // adding money to the transfer wallet to user
                if (walletTransaction.getToWallet().equalsIgnoreCase("CAPITAL_WALLET")) {
                    double currentCapitalAmount = walletRepository.fetchUserCapitalWalletAmount(walletTransaction.getToUserId(), "ACTIVE");
                    double totalCapitalAmount = currentCapitalAmount + walletTransaction.getAmount();
                    walletRepository.updateNodeWalletOfUser(totalCapitalAmount, walletTransaction.getToUserId());
                }

            } else if (walletTransaction.getToWallet().equalsIgnoreCase("NODE_WALLET")) {
                double currentFromWalletAmount = walletRepository.fetchUserNodeWalletAmount(walletTransaction.getFromUserId(), "ACTIVE");
                double totalAmountLeftInFromWallet = currentFromWalletAmount - walletTransaction.getAmount();
                if (totalAmountLeftInFromWallet < 0) {
                    Util.setMessage(finalResponse, "100", "Error: You do not have enough Node Amount to transfer.");
                    return finalResponse;
                }
                walletRepository.updateNodeWalletOfUser(totalAmountLeftInFromWallet, walletTransaction.getFromUserId());

                // adding money to the transfer wallet
                if (walletTransaction.getToWallet().equalsIgnoreCase("NODE_WALLET")) {
                    double currentNodeAmount = walletRepository.fetchUserNodeWalletAmount(walletTransaction.getToUserId(), "ACTIVE");
                    double totalNodeAmount = currentNodeAmount + walletTransaction.getAmount();
                    walletRepository.updateNodeWalletOfUser(totalNodeAmount, walletTransaction.getToUserId());
                }
//                if (walletTransaction.getToWallet().equalsIgnoreCase("CAPITAL_WALLET")) {
//                    double currentNodeAmount = walletRepository.fetchUserNodeWalletAmount(walletTransaction.getToUserId(), "ACTIVE");
//                    double totalNodeAmount = currentNodeAmount + walletTransaction.getAmount();
//                    walletRepository.updateNodeWalletOfUser(totalNodeAmount, walletTransaction.getToUserId());
//                } else if (walletTransaction.getToWallet().equalsIgnoreCase("MINE_WALLET")) {
//                    double currentMineAmount = walletRepository.fetchUserMineWalletAmount(walletTransaction.getToUserId(), "ACTIVE");
//                    double totalmineAmount = currentMineAmount + walletTransaction.getAmount();
//                    walletRepository.updateMineWalletOfUser(totalmineAmount, walletTransaction.getToUserId());
//                }

            }
//            else if (walletTransaction.getFromWallet().equalsIgnoreCase("MINE_WALLET")) {
//                double currentFromWalletAmount = walletRepository.fetchUserMineWalletAmount(walletTransaction.getFromUserId(), "ACTIVE");
//                double totalAmountLeftInFromWallet = currentFromWalletAmount - walletTransaction.getAmount();
//                walletRepository.updateMineWalletOfUser(totalAmountLeftInFromWallet, walletTransaction.getFromUserId());
//
//                // adding money to the transfer wallet
//                if (walletTransaction.getToWallet().equalsIgnoreCase("NODE_WALLET")) {
//                    double currentNodeAmount = walletRepository.fetchUserNodeWalletAmount(walletTransaction.getToUserId(), "ACTIVE");
//                    double totalNodeAmount = currentNodeAmount + walletTransaction.getAmount();
//                    walletRepository.updateNodeWalletOfUser(totalNodeAmount, walletTransaction.getToUserId());
//                } else if (walletTransaction.getToWallet().equalsIgnoreCase("CAPITAL_WALLET")) {
//                    double currentMineAmount = walletRepository.fetchUserMineWalletAmount(walletTransaction.getToUserId(), "ACTIVE");
//                    double totalmineAmount = currentMineAmount + walletTransaction.getAmount();
//                    walletRepository.updateMineWalletOfUser(totalmineAmount, walletTransaction.getToUserId());
//                }
//
//            }
            else {
                Util.setMessage(finalResponse, "100", "Error:Unable to get From wallet name , Please contact Admin.");
                return finalResponse;
            }

        } else {
            Util.setMessage(finalResponse, "100", "Error:Transfer not found.");
            return finalResponse;
        }
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinalResponse confirmUser(String nodeId) {
        FinalResponse finalResponse = new FinalResponse();
        User user = userRepository.findByNodeIdAndActiveStateCodeFkId(nodeId, "ACTIVE");
        int totalActiveTeamLeft= userRepository. totalLeftTeam(nodeId);
        int totalActiveTeamRight= userRepository. totalRightTeam(nodeId);
        if (Util.isDefined(user)) {
            Optional<User> users = Optional.ofNullable(user);
            users.map(existing -> {
                existing.setDateOfActivation(LocalDateTime.now());
                existing.setUserStatus("ACTIVE");
                existing.setLeftVolume( BigDecimal.valueOf(totalActiveTeamLeft));
                existing.setRightVolume( BigDecimal.valueOf(totalActiveTeamRight));
                if(Util.isDefined(existing.getReferralCode())) {
                    existing.setParentNodeId(existing.getReferralCode());
                }
                else{
                    existing.setParentNodeId("NODE24770625");
                }
                return userRepository.save(existing);
            });
//            Wallet wallet = new Wallet();
//            wallet.setUserNodeCode(nodeId);
//            finalResponse = individualService.addWalletData(wallet);
//            if (!finalResponse.getStatusCode().equals("200")) {
//                return finalResponse;
//            }

        } else {
            Util.setMessage(finalResponse, "100", "Error: User not found.");
            return finalResponse;
        }
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    public FinalResponse getAdminDashboardCount(Integer inputPkIdInt, String inputFkId, int page, int size, String filterBy, String searchValue){
        FinalResponse finalResponse = new FinalResponse();
        AdminDashboardInfo adminDashboardInfo=new AdminDashboardInfo();
        int totalActiveUser=userRepository.countByActiveStateCodeFkId("ACTIVE");
        adminDashboardInfo.setTotalActiveUser(totalActiveUser);
        int totalInActiveUser=userRepository.countByActiveStateCodeFkId("INACTIVE");
        adminDashboardInfo.setTotalInactiveUser(totalInActiveUser);
        int adminCount = userRepository.countByRoleName("ADMIN_USER");
        int normalCount = userRepository.countByRoleName("NORMAL_USER");
        adminDashboardInfo.setTotalAdminUser(adminCount);
        adminDashboardInfo.setTotalNormalUser(normalCount);
        long totalUserCount= userRepository.count();
        adminDashboardInfo.setTotalUser(totalUserCount);
        Double nodeWallet= walletRepository.getTotalNodeWallet();
        Double mineWallet= walletRepository.getTotalMineWallet();
        Double capitalWallet=  walletRepository.getTotalCapitalWallet();
        Double totalDebit =walletRepository.getTotalDebit();
        Double totalCredit=walletRepository.getTotalTotalCredit();
        adminDashboardInfo.setNodewallet(nodeWallet);
        adminDashboardInfo.setTotalMineWallet(mineWallet);
        adminDashboardInfo.setCapitalWallet(capitalWallet);
        if(Util.isDefined(nodeWallet) && Util.isDefined(mineWallet) && Util.isDefined(capitalWallet)){
            Double totalWallet=nodeWallet+mineWallet+capitalWallet;
            adminDashboardInfo.setTotalWallet(totalWallet);
        }
        adminDashboardInfo.setTotalDebit(totalDebit);
        adminDashboardInfo.setTotalCredit(totalCredit);

        finalResponse.setResponse(adminDashboardInfo);


        return finalResponse;
    }

    @Override
    public FinalResponse getSubscriptionDefinition(String inputPkId, String inputFkId, int page, int size, String filterBy, String searchValue) {
        FinalResponse<SubscriptionDefinition> finalResponse = new FinalResponse<>();
        Pageable pageable = Util.getPageable(size, page);
        List<SubscriptionDefinition> subscriptionDefinitionList = populateSubscriptionDefinitionView(inputPkId,inputFkId, filterBy,searchValue, pageable);
        int count = populateSubscriptionDefinitionCount(inputPkId, inputFkId, filterBy);
        finalResponse.setData(subscriptionDefinitionList);
        finalResponse.setCount( count);
        Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    private int populateSubscriptionDefinitionCount(String inputPkId, String inputFkId, String filterBy) {
        int count = 0;
        if (Util.isDefined(inputPkId)) {
            count = subscriptionDefinitionRepo.countBySubscriptionDefinitionPkIdAndActiveStateCodeFkId(inputPkId, "ACTIVE");
        }
        else if(Util.isDefined(inputFkId)) {
            count = subscriptionDefinitionRepo.countByActiveStateCodeFkIdAndSubscriptionCode("ACTIVE", inputFkId);
        }
        else {
            count = subscriptionDefinitionRepo.countByActiveStateCodeFkId("ACTIVE");
        }

        return count;
    }

    private List<SubscriptionDefinition> populateSubscriptionDefinitionView(String inputPkId, String inputFkId, String filterBy, String searchValue, Pageable pageable) {
        List<SubscriptionDefinition> subscriptionDefinitionList = new ArrayList<>();
        if (Util.isDefined(inputPkId)) {
            SubscriptionDefinition subscriptionDefinition = subscriptionDefinitionRepo.findBySubscriptionDefinitionPkIdAndActiveStateCodeFkId(inputPkId, "ACTIVE");
            subscriptionDefinitionList.add(subscriptionDefinition);
        }
        else if (Util.isDefined(inputFkId)) {
            subscriptionDefinitionList = subscriptionDefinitionRepo.findByActiveStateCodeFkIdAndSubscriptionCode("ACTIVE", inputFkId,pageable);
        }
        else {
            subscriptionDefinitionList = subscriptionDefinitionRepo.findByActiveStateCodeFkId("ACTIVE", pageable);
        }
        return subscriptionDefinitionList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinalResponse addSubscriptionDefinition(SubscriptionDefinition subscriptionDefinition) {
        FinalResponse finalResponse = new FinalResponse();
        String vLastModifiedDateTime = Util.getCurrentUTCTimestampString();
        subscriptionDefinition.setEffectiveDateTime(vLastModifiedDateTime);
        //effective date cannot be greater than present date
        if (Util.compareDate(subscriptionDefinition.getEffectiveDateTime(), vLastModifiedDateTime) > 0) {
            Util.setMessage(finalResponse, "100", "Error: Effective date time cannot be greater than the present moment.");
            return finalResponse;
        }
        subscriptionDefinition.setSubscriptionCode(subscriptionDefinition.getSubscriptionName().toUpperCase());
        subscriptionDefinition.setSubscriptionStartDateTime(LocalDateTime.now());
        subscriptionDefinition.setSubscriptionEndDateTime(LocalDateTime.now().plusYears(1));
        Util.setCommonDefaultAttributes(subscriptionDefinition);
        subscriptionDefinitionRepo.save(subscriptionDefinition);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinalResponse updateSubscriptionDefinition(Integer id, SubscriptionDefinition subscriptionDefinition) {
        FinalResponse finalResponse = new FinalResponse();
        subscriptionDefinitionRepo.findById(id)
                .map(existing -> {
                    existing.setSubscriptionName(subscriptionDefinition.getSubscriptionName());
                    existing.setSubscriptionCode(subscriptionDefinition.getSubscriptionCode());
                    existing.setSubscriptionAmount(subscriptionDefinition.getSubscriptionAmount());
                    existing.setSubscriptionStartDateTime(subscriptionDefinition.getSubscriptionStartDateTime());
                    existing.setSubscriptionEndDateTime(subscriptionDefinition.getSubscriptionEndDateTime());
                    return subscriptionDefinitionRepo.save(existing);
                }).orElseThrow(() -> new RuntimeException("SubscriptionDefinition not found"));
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FinalResponse deleteSubscriptionDefinition(Integer id) {
        FinalResponse finalResponse = new FinalResponse();
        subscriptionDefinitionRepo.deleteById(id);
        finalResponse = Util.setSuccessMessage(finalResponse);
        return finalResponse;
    }


}
