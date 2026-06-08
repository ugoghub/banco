package service;

import model.AccountType;
import model.TransactionType;
import model.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.AccountRepository;
import repository.ClientRepository;
import repository.TransactionRepository;
import service.dto.StatementData;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionServiceInterestTest {

    private ClientService clientService;
    private AccountService accountService;
    private TransactionService transactionService;
    private AccountRepository accountRepository;
    private TransactionRepository transactionRepository;

    private static final Cpf cpf =
            new Cpf("52998224725");

    private static final PersonName name =
            new PersonName("Pedro Silva");

    private static final Email email =
            new Email("pedro@gmail.com");


    private final Clock january =
            Clock.fixed(
                    Instant.parse("2026-01-01T10:00:00Z"),
                    ZoneOffset.UTC
            );

    private final Clock february =
            Clock.fixed(
                    Instant.parse("2026-02-01T10:00:00Z"),
                    ZoneOffset.UTC
            );

    @BeforeEach
    void setup() {

        ClientRepository clientRepository = new ClientRepository();
        accountRepository = new AccountRepository();
        transactionRepository = new TransactionRepository();

        clientService =
                new ClientService(clientRepository);

        accountService =
                new AccountService(
                        accountRepository,
                        january
                );

        transactionService =
                new TransactionService(
                        accountService,
                        transactionRepository,
                        january
                );
    }


    // =========================
    // Deposit
    // =========================

    @Test
    void shouldApplyPendingInterestBeforeDeposit() {

        AccountIdentity account = createSavingsAccountWithInitialBalance();


        moveTo(february);

        transactionService.deposit(account, Money.of("100"));

        assertEquals(
                Money.of("1105").value(),
                transactionService.getAccountBalance(account).value()
        );
    }

    @Test
    void shouldCreateInterestTransactionBeforeDeposit() {

        AccountIdentity account = createSavingsAccountWithInitialBalance();


        moveTo(february);

        transactionService.deposit(account, Money.of("100"));

        List<StatementData> statement =
                transactionService.getTransactionHistoryByAccountIdentity(
                        account
                );

        assertEquals(
                TransactionType.INTEREST,
                statement.get(1).type()
        );

        assertEquals(
                TransactionType.DEPOSIT,
                statement.get(2).type()
        );
    }

    // =========================
    // Withdraw
    // =========================

    @Test
    void shouldCreateInterestTransactionBeforeWithdraw() {

        AccountIdentity account = createSavingsAccountWithInitialBalance();

        moveTo(february);

        transactionService.withdraw(
                account,
                Money.of("5")
        );

        List<StatementData> statement =
                transactionService.getTransactionHistoryByAccountIdentity(
                        account
                );

        assertEquals(
                TransactionType.INTEREST,
                statement.get(1).type()
        );

        assertEquals(
                TransactionType.WITHDRAW,
                statement.get(2).type()
        );
    }

    @Test
    void shouldApplyPendingInterestBeforeWithdraw() {

        AccountIdentity account = createSavingsAccountWithInitialBalance();

        moveTo(february);

        transactionService.withdraw(
                account,
                Money.of("5")
        );

        assertEquals(
                Money.of("1000"),
                transactionService.getAccountBalance(account)
        );
    }

    // =========================
    // Balance
    // =========================

    @Test
    void shouldApplyPendingInterestWhenGettingBalance() {

        AccountIdentity account =
                createSavingsAccountWithInitialBalance();

        moveTo(february);

        assertEquals(
                Money.of("1005").value(),
                transactionService.getAccountBalance(account).value()
        );
    }

    @Test
    void shouldNotApplyInterestTwiceWhenBalanceIsCheckedMultipleTimes() {

        AccountIdentity account = createSavingsAccountWithInitialBalance();


        moveTo(february);

        transactionService.getAccountBalance(account);
        transactionService.getAccountBalance(account);

        List<StatementData> statement =
                history(account);

        long interestCount =
                statement.stream()
                        .filter(t ->
                                t.type() == TransactionType.INTEREST
                        )
                        .count();

        assertEquals(1, interestCount);
    }

    // =========================
    // Transfer
    // =========================

    @Test
    void shouldApplyPendingInterestBeforeTransferBetweenSavingsAccounts() {

        AccountIdentity from = createSavingsAccountWithInitialBalance();

        AccountIdentity to = createSecondSavingsAccountWithNoBalance();

        moveTo(february);

        transactionService.transfer(
                from,
                to,
                Money.of("100")
        );

        assertEquals(
                Money.of("905").value(),
                transactionService.getAccountBalance(from).value()
        );

        assertEquals(
                Money.of("100").value(),
                transactionService.getAccountBalance(to).value()
        );
    }

    @Test
    void shouldCreateInterestTransactionsBeforeTransfer() {

        AccountIdentity from =
                createSavingsAccountWithInitialBalance();

        AccountIdentity to =
                createSecondSavingsAccountWithNoBalance();

        transactionService.deposit(
                to,
                Money.of("1000")
        );

        moveTo(february);

        transactionService.transfer(
                from,
                to,
                Money.of("100")
        );

        List<StatementData> fromHistory =
                history(from);

        List<StatementData> toHistory =
                history(to);

        assertEquals(
                TransactionType.INTEREST,
                fromHistory.get(1).type()
        );

        assertEquals(
                TransactionType.TRANSFER_SENT,
                fromHistory.get(2).type()
        );

        assertEquals(
                TransactionType.INTEREST,
                toHistory.get(1).type()
        );

        assertEquals(
                TransactionType.TRANSFER_RECEIVED,
                toHistory.get(2).type()
        );
    }

    // =========================
    // History
    // =========================

    @Test
    void shouldCreateOneInterestTransactionPerPendingMonth() {

        AccountIdentity account = createSavingsAccountWithInitialBalance();

        Clock july = Clock.fixed(
                Instant.parse("2026-07-01T10:00:00Z"),
                ZoneOffset.UTC
        );

        moveTo(july);

        transactionService.getAccountBalance(account);

        List<StatementData> statement =
                history(account);

        long interestCount =
                statement.stream()
                        .filter(t ->
                                t.type() == TransactionType.INTEREST
                        )
                        .count();

        assertEquals(6, interestCount);

        assertEquals(
                Money.of("1030.38"),
                transactionService.getAccountBalance(account)
        );
    }

    @Test
    void shouldApplyPendingInterestBeforeGettingStatement() {

        AccountIdentity account = createSavingsAccountWithInitialBalance();

       moveTo(february);

        List<StatementData> statement =
                transactionService.getTransactionHistoryByAccountIdentity(
                        account
                );

        assertEquals(2, statement.size());

        assertEquals(
                TransactionType.INTEREST,
                statement.getLast().type()
        );
    }

    // =========================
    // Helpers
    // =========================

    private AccountIdentity createSavingsAccountWithInitialBalance() {

        clientService.createClient(
                name,
                cpf,
                email
        );

        UUID clientId =
                clientService.getClientId(cpf);

        AccountIdentity account =
                accountService.createAccount(
                        clientId,
                        AccountType.SAVINGS
                );

        transactionService.deposit(
                account,
                Money.of("1000")
        );

        return account;
    }

    private AccountIdentity createSecondSavingsAccountWithNoBalance() {

        Cpf cpf =
                new Cpf("76887934086");

        PersonName name =
                new PersonName("Ana Ferreira");

        Email email =
                new Email("ana@gmail.com");

        clientService.createClient(
                name,
                cpf,
                email
        );

        UUID clientId =
                clientService.getClientId(cpf);

        return accountService.createAccount(
                clientId,
                AccountType.SAVINGS
        );
    }

    private void moveTo(Clock clock) {

        accountService =
                new AccountService(
                        accountRepository,
                        clock
                );

        transactionService =
                new TransactionService(
                        accountService,
                        transactionRepository,
                        clock
                );
    }

    private List<StatementData> history(
            AccountIdentity accountIdentity
    ) {

        return transactionService
                .getTransactionHistoryByAccountIdentity(accountIdentity);
    }
}
