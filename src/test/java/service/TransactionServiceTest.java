package service;

import exception.AccountNotFoundException;
import exception.InsufficientBalanceException;
import exception.InvalidAmountException;
import exception.InvalidTransferException;
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

import static org.junit.jupiter.api.Assertions.*;

class TransactionServiceTest {

    private ClientService clientService;
    private AccountService accountService;
    private TransactionService transactionService;

    private static final Cpf cpf1 =
            new Cpf("52998224725");

    private static final Cpf cpf2 =
            new Cpf("76887934086");

    private static final PersonName name1 =
            new PersonName("Pedro Silva");

    private static final PersonName name2 =
            new PersonName("Ana Ferreira");

    private static final Email email1 =
            new Email("pedro@gmail.com");

    private static final Email email2 =
            new Email("ana@gmail.com");


    private static final AccountIdentity NONEXISTENT_ACCOUNT =
            new AccountIdentity(
                    "01",
                    "000001-1"
            );


    @BeforeEach
    void setup() {

        Clock clock = Clock.systemUTC();

        ClientRepository clientRepository = new ClientRepository();
        AccountRepository accountRepository = new AccountRepository();
        TransactionRepository transactionRepository = new TransactionRepository();

        clientService =
                new ClientService(clientRepository);

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

    // =========================
    // Deposit
    // =========================

    @Test
    void shouldDepositMoney() {

        AccountIdentity account =
                createCheckingAccount();

        deposit(account, "100");

        assertEquals(
                money("100"),
                balance(account)
        );
    }

    @Test
    void shouldNotDepositZeroValue() {

        AccountIdentity account =
                createCheckingAccount();

        assertThrows(
                InvalidAmountException.class,
                () -> deposit(account, "0")
        );
    }

    @Test
    void shouldNotDepositNegativeValue() {

        AccountIdentity account =
                createCheckingAccount();

        assertThrows(
                InvalidAmountException.class,
                () -> deposit(account, "-100")
        );
    }

    @Test
    void shouldCreateDepositTransactionHistory() {

        AccountIdentity account =
                createCheckingAccount();

        deposit(account, "100");

        List<StatementData> history =
                history(account);

        assertEquals(1, history.size());

        StatementData transaction =
                history.getFirst();

        assertEquals(
                TransactionType.DEPOSIT,
                transaction.type()
        );

        assertEquals(
                account,
                transaction.destination()
        );

        assertNull(transaction.source());
    }

    @Test
    void shouldThrowExceptionWhenDepositingIntoNonexistentAccount() {

        assertThrows(
                AccountNotFoundException.class,
                () -> deposit(
                        NONEXISTENT_ACCOUNT,
                        "100"
                )
        );
    }

    @Test
    void shouldRoundMoneyOperationsCorrectly() {

        AccountIdentity account =
                createCheckingAccount();

        deposit(account, "0.015");

        assertEquals(
                Money.of("0.02"),
                balance(account)
        );
    }

    @Test
    void shouldApplyPendingInterestBeforeDeposit() {

        Clock january =
                Clock.fixed(
                        Instant.parse("2026-01-01T10:00:00Z"),
                        ZoneOffset.UTC
                );

        AccountRepository accountRepository =
                new AccountRepository();

        TransactionRepository transactionRepository =
                new TransactionRepository();

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

        clientService.createClient(
                name1,
                cpf1,
                email1
        );

        UUID clientId =
                clientService.getClientId(cpf1);

        AccountIdentity account =
                accountService.createAccount(
                        clientId,
                        AccountType.SAVINGS
                );

        transactionService.deposit(
                account,
                Money.of("1000")
        );

        Clock february =
                Clock.fixed(
                        Instant.parse("2026-02-02T10:00:00Z"),
                        ZoneOffset.UTC
                );

        accountService =
                new AccountService(
                        accountRepository,
                        february
                );

        transactionService =
                new TransactionService(
                        accountService,
                        transactionRepository,
                        february
                );

        transactionService.deposit(
                account,
                Money.of("100")
        );

        assertEquals(
                Money.of("1105"),
                transactionService.getAccountBalance(account)
        );
    }

    @Test
    void shouldCreateInterestTransactionBeforeDeposit() {

        Clock january =
                Clock.fixed(
                        Instant.parse("2026-01-01T10:00:00Z"),
                        ZoneOffset.UTC
                );

        AccountRepository accountRepository =
                new AccountRepository();

        TransactionRepository transactionRepository =
                new TransactionRepository();

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

        clientService.createClient(
                name1,
                cpf1,
                email1
        );

        UUID clientId =
                clientService.getClientId(cpf1);

        AccountIdentity account =
                accountService.createAccount(
                        clientId,
                        AccountType.SAVINGS
                );

        transactionService.deposit(
                account,
                Money.of("1000")
        );

        Clock february =
                Clock.fixed(
                        Instant.parse("2026-02-02T10:00:00Z"),
                        ZoneOffset.UTC
                );

        accountService =
                new AccountService(
                        accountRepository,
                        february
                );

        transactionService =
                new TransactionService(
                        accountService,
                        transactionRepository,
                        february
                );

        transactionService.deposit(
                account,
                Money.of("100")
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
                TransactionType.DEPOSIT,
                statement.get(2).type()
        );
    }

    // =========================
    // Withdraw
    // =========================

    @Test
    void shouldWithdrawMoney() {

        AccountIdentity account =
                createCheckingAccount();

        deposit(account, "200");

        withdraw(account, "50");

        assertEquals(
                money("150"),
                balance(account)
        );
    }

    @Test
    void shouldNotWithdrawZeroValue() {

        AccountIdentity account =
                createCheckingAccount();

        assertThrows(
                InvalidAmountException.class,
                () -> withdraw(account, "0")
        );
    }

    @Test
    void shouldNotWithdrawNegativeValue() {

        AccountIdentity account =
                createCheckingAccount();

        assertThrows(
                InvalidAmountException.class,
                () -> withdraw(account, "-50")
        );
    }

    @Test
    void checkingAccountShouldAllowNegativeBalanceUntilLimit() {

        AccountIdentity account =
                createCheckingAccount();

        withdraw(account, "1000");

        assertEquals(
                money("-1000"),
                balance(account)
        );
    }

    @Test
    void checkingAccountShouldNotExceedOverdraftLimit() {

        AccountIdentity account =
                createCheckingAccount();

        assertThrows(
                InsufficientBalanceException.class,
                () -> withdraw(account, "1000.01")
        );
    }

    @Test
    void savingsAccountShouldNotAllowNegativeBalance() {

        AccountIdentity account =
                createSavingsAccount();

        assertThrows(
                InsufficientBalanceException.class,
                () -> withdraw(account, "1")
        );
    }

    @Test
    void shouldCreateWithdrawTransactionHistory() {

        AccountIdentity account =
                createCheckingAccount();

        deposit(account, "500");

        withdraw(account, "200");

        List<StatementData> history =
                history(account);

        assertEquals(2, history.size());

        StatementData withdraw =
                history.get(1);

        assertEquals(
                TransactionType.WITHDRAW,
                withdraw.type()
        );

        assertEquals(
                account,
                withdraw.source()
        );

        assertNull(withdraw.destination());
    }

    @Test
    void shouldThrowExceptionWhenWithdrawingFromNonexistentAccount() {

        assertThrows(
                AccountNotFoundException.class,
                () -> withdraw(
                        NONEXISTENT_ACCOUNT,
                        "50"
                )
        );
    }

    @Test
    void shouldCreateInterestTransactionBeforeWithdraw() {

        Clock january =
                Clock.fixed(
                        Instant.parse("2026-01-01T10:00:00Z"),
                        ZoneOffset.UTC
                );

        AccountRepository accountRepository =
                new AccountRepository();

        TransactionRepository transactionRepository =
                new TransactionRepository();

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

        clientService.createClient(
                name1,
                cpf1,
                email1
        );

        UUID clientId =
                clientService.getClientId(cpf1);

        AccountIdentity account =
                accountService.createAccount(
                        clientId,
                        AccountType.SAVINGS
                );

        transactionService.deposit(
                account,
                Money.of("1000")
        );

        Clock february =
                Clock.fixed(
                        Instant.parse("2026-02-02T10:00:00Z"),
                        ZoneOffset.UTC
                );

        accountService =
                new AccountService(
                        accountRepository,
                        february
                );

        transactionService =
                new TransactionService(
                        accountService,
                        transactionRepository,
                        february
                );

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

    // =========================
    // Transfer
    // =========================

    @Test
    void shouldNotTransferToSameAccount() {

        AccountIdentity account =
                createCheckingAccount();

        assertThrows(
                InvalidTransferException.class,
                () -> transfer(account, account, "10")
        );
    }

    @Test
    void shouldNotTransferNegativeValue() {

        AccountIdentity from =
                createCheckingAccount();

        AccountIdentity to =
                createSavingsAccount();

        assertThrows(
                InvalidAmountException.class,
                () -> transfer(from, to, "-100")
        );
    }

    @Test
    void shouldNotTransferZeroValue() {

        AccountIdentity from =
                createCheckingAccount();

        AccountIdentity to =
                createSavingsAccount();

        assertThrows(
                InvalidAmountException.class,
                () -> transfer(from, to, "0")
        );
    }

    @Test
    void failedTransferShouldNotGenerateHistory() {

        AccountIdentity from =
                createSavingsAccount();

        AccountIdentity to =
                createCheckingAccount();

        deposit(from, "100");

        assertThrows(
                InsufficientBalanceException.class,
                () -> transfer(
                        from,
                        to,
                        "200"
                )
        );

        List<StatementData> fromHistory =
                history(from);

        List<StatementData> toHistory =
                history(to);

        assertEquals(1, fromHistory.size());

        assertTrue(toHistory.isEmpty());

        assertEquals(
                TransactionType.DEPOSIT,
                fromHistory.getFirst().type()
        );
    }

    @Test
    void transferShouldUpdateBothBalances() {

        AccountIdentity from =
                createCheckingAccount();

        AccountIdentity to =
                createSavingsAccount();

        deposit(from, "1000");

        transfer(from, to, "300");

        assertEquals(
                money("700"),
                balance(from)
        );

        assertEquals(
                money("300"),
                balance(to)
        );
    }

    @Test
    void transferShouldNotCreditDestinationWhenWithdrawFails() {

        AccountIdentity from =
                createSavingsAccount();

        AccountIdentity to =
                createCheckingAccount();

        deposit(from, "100");

        assertThrows(
                InsufficientBalanceException.class,
                () -> transfer(from, to, "200")
        );

        assertEquals(
                money("100"),
                balance(from)
        );

        assertEquals(
                Money.ZERO,
                balance(to)
        );
    }

    @Test
    void transferShouldGenerateDifferentTransactionIds() {

        AccountIdentity from =
                createCheckingAccount();

        AccountIdentity to =
                createSavingsAccount();

        deposit(from, "200");

        transfer(from, to, "50");

        List<StatementData> fromHistory =
                history(from);

        List<StatementData> toHistory =
                history(to);

        StatementData sent =
                fromHistory.getLast();

        StatementData received =
                toHistory.getFirst();

        assertNotEquals(
                sent.id(),
                received.id()
        );
    }

    @Test
    void shouldCreateTransferHistoryForBothAccounts() {

        AccountIdentity from =
                createSavingsAccount();

        AccountIdentity to =
                createCheckingAccount();

        deposit(from, "200");

        transfer(from, to, "50");

        List<StatementData> fromHistory =
                history(from);

        List<StatementData> toHistory =
                history(to);

        assertEquals(2, fromHistory.size());
        assertEquals(1, toHistory.size());

        assertEquals(
                TransactionType.TRANSFER_SENT,
                fromHistory.getLast().type()
        );

        assertEquals(
                TransactionType.TRANSFER_RECEIVED,
                toHistory.getFirst().type()
        );
    }

    @Test
    void shouldThrowExceptionWhenDestinationAccountDoesNotExist() {

        AccountIdentity from =
                createCheckingAccount();

        deposit(from, "100");

        assertThrows(
                AccountNotFoundException.class,
                () -> transfer(
                        from,
                        NONEXISTENT_ACCOUNT,
                        "50"
                )
        );
    }

    @Test
    void shouldNotGenerateTransferHistoryWhenDestinationDoesNotExist() {

        AccountIdentity from =
                createCheckingAccount();

        deposit(from, "100");

        assertThrows(
                AccountNotFoundException.class,
                () -> transfer(
                        from,
                        NONEXISTENT_ACCOUNT,
                        "50"
                )
        );

        List<StatementData> history =
                history(from);

        assertEquals(1, history.size());

        assertEquals(
                TransactionType.DEPOSIT,
                history.getFirst().type()
        );
    }

    @Test
    void transferShouldGenerateConsistentTransactionData() {

        AccountIdentity from =
                createCheckingAccount();

        AccountIdentity to =
                createSavingsAccount();

        deposit(from, "200");

        transfer(from, to, "50");

        List<StatementData> fromHistory =
                history(from);

        List<StatementData> toHistory =
                history(to);

        StatementData sent =
                fromHistory.getLast();

        StatementData received =
                toHistory.getFirst();

        assertEquals(
                from,
                sent.source()
        );

        assertEquals(
                to,
                sent.destination()
        );

        assertEquals(
                from,
                received.source()
        );

        assertEquals(
                to,
                received.destination()
        );
    }

    @Test
    void shouldMaintainCorrectBalanceAfterMultipleOperations() {

        AccountIdentity account =
                createCheckingAccount();

        deposit(account, "1000");

        withdraw(account, "200");

        deposit(account, "50");

        withdraw(account, "100");

        assertEquals(
                Money.of("750"),
                balance(account)
        );
    }

    @Test
    void shouldPreserveExactMonetaryPrecisionAfterMultipleOperations(){

        AccountIdentity account =
                createCheckingAccount();

        deposit(account, "0.10");
        deposit(account, "0.20");
        withdraw(account, "0.30");

        assertEquals(Money.ZERO, balance(account));
    }

    @Test
    void transferTransactionsShouldShareSameOperationId() {

        AccountIdentity from =
                createCheckingAccount();

        AccountIdentity to =
                createSavingsAccount();

        deposit(from, "200");

        transfer(from, to, "50");

        List<StatementData> fromHistory =
                history(from);

        List<StatementData> toHistory =
                history(to);

        StatementData sent =
                fromHistory.getLast();

        StatementData received =
                toHistory.getFirst();

        assertEquals(
                sent.operationId(),
                received.operationId()
        );
    }

    @Test
    void shouldKeepTransferHistoryOrder() {

        AccountIdentity from =
                createCheckingAccount();

        AccountIdentity to =
                createSavingsAccount();

        deposit(from, "500");

        transfer(from, to, "100");

        transfer(from, to, "50");

        List<StatementData> history =
                history(from);

        assertEquals(
                TransactionType.DEPOSIT,
                history.get(0).type()
        );

        assertEquals(
                TransactionType.TRANSFER_SENT,
                history.get(1).type()
        );

        assertEquals(
                TransactionType.TRANSFER_SENT,
                history.get(2).type()
        );
    }

    @Test
    void shouldApplyPendingInterestBeforeWithdraw() {

        Clock january =
                Clock.fixed(
                        Instant.parse("2026-01-01T10:00:00Z"),
                        ZoneOffset.UTC
                );

        ClientRepository clientRepository =
                new ClientRepository();

        AccountRepository accountRepository =
                new AccountRepository();

        TransactionRepository transactionRepository =
                new TransactionRepository();

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

        clientService.createClient(
                new PersonName("Hugo Silva"),
                cpf1,
                new Email("hugo@gmail.com")
        );

        UUID clientId = clientService.getClientId(cpf1);

        AccountIdentity account =
                accountService.createAccount(
                        clientId,
                        AccountType.SAVINGS
                );

        transactionService.deposit(
                account,
                Money.of("1000")
        );

        Clock february =
                Clock.fixed(
                        Instant.parse("2026-02-02T10:00:00Z"),
                        ZoneOffset.UTC
                );

        accountService =
                new AccountService(
                        accountRepository,
                        february
                );

        transactionService =
                new TransactionService(
                        accountService,
                        transactionRepository,
                        february
                );

        transactionService.withdraw(
                account,
                Money.of("5")
        );

        assertEquals(
                Money.of("1000"),
                transactionService.getAccountBalance(account)
        );
    }

    @Test
    void shouldApplyPendingInterestWhenGettingBalance() {

        Clock january =
                Clock.fixed(
                        Instant.parse("2026-01-01T10:00:00Z"),
                        ZoneOffset.UTC
                );

        AccountRepository accountRepository =
                new AccountRepository();

        ClientRepository clientRepository =
                new ClientRepository();

        TransactionRepository transactionRepository =
                new TransactionRepository();

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

        clientService.createClient(
                name1,
                cpf1,
                email1
        );

        UUID clientId =
                clientService.getClientId(cpf1);

        AccountIdentity account =
                accountService.createAccount(
                        clientId,
                        AccountType.SAVINGS
                );

        transactionService.deposit(
                account,
                Money.of("1000")
        );

        Clock february =
                Clock.fixed(
                        Instant.parse("2026-02-02T10:00:00Z"),
                        ZoneOffset.UTC
                );

        accountService =
                new AccountService(
                        accountRepository,
                        february
                );

        transactionService =
                new TransactionService(
                        accountService,
                        transactionRepository,
                        february
                );

        Money balance =
                transactionService.getAccountBalance(account);

        assertEquals(
                Money.of("1005"),
                balance
        );
    }

    @Test
    void shouldApplyPendingInterestBeforeTransferBetweenSavingsAccounts() {

        Clock january =
                Clock.fixed(
                        Instant.parse("2026-01-01T10:00:00Z"),
                        ZoneOffset.UTC
                );

        ClientRepository clientRepository =
                new ClientRepository();

        AccountRepository accountRepository =
                new AccountRepository();

        TransactionRepository transactionRepository =
                new TransactionRepository();

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

        clientService.createClient(
                name1,
                cpf1,
                email1
        );

        UUID clientId = clientService.getClientId(cpf1);

        clientService.createClient(
                name2,
                cpf2,
                email2
        );

        UUID clientId2  = clientService.getClientId(cpf2);

        AccountIdentity from =
                accountService.createAccount(
                        clientId,
                        AccountType.SAVINGS
                );

        AccountIdentity to =
                accountService.createAccount(
                        clientId2,
                        AccountType.SAVINGS
                );

        transactionService.deposit(
                from,
                Money.of("1000")
        );

        Clock february =
                Clock.fixed(
                        Instant.parse("2026-02-02T10:00:00Z"),
                        ZoneOffset.UTC
                );

        accountService =
                new AccountService(
                        accountRepository,
                        february
                );

        transactionService =
                new TransactionService(
                        accountService,
                        transactionRepository,
                        february
                );

        transactionService.transfer(
                from,
                to,
                Money.of("100")
        );

        assertEquals(
                Money.of("905"),
                transactionService.getAccountBalance(from)
        );

        assertEquals(
                Money.of("100"),
                transactionService.getAccountBalance(to)
        );
    }

    // =========================
    // History
    // =========================

    @Test
    void shouldReturnEmptyHistory() {

        AccountIdentity account =
                createCheckingAccount();

        assertTrue(
                history(account).isEmpty()
        );
    }

    @Test
    void shouldKeepTransactionOrder() {

        AccountIdentity account =
                createCheckingAccount();

        deposit(account, "100");

        withdraw(account, "50");

        List<StatementData> history =
                history(account);

        assertEquals(
                TransactionType.DEPOSIT,
                history.get(0).type()
        );

        assertEquals(
                TransactionType.WITHDRAW,
                history.get(1).type()
        );
    }

    @Test
    void transactionsShouldHaveUniqueIds() {

        AccountIdentity account =
                createCheckingAccount();

        deposit(account, "100");

        deposit(account, "200");

        List<StatementData> history =
                history(account);

        assertNotEquals(
                history.get(0).id(),
                history.get(1).id()
        );
    }

    @Test
    void shouldApplyPendingInterestBeforeGettingStatement() {

        Clock january =
                Clock.fixed(
                        Instant.parse("2026-01-01T10:00:00Z"),
                        ZoneOffset.UTC
                );

        AccountRepository accountRepository =
                new AccountRepository();

        TransactionRepository transactionRepository =
                new TransactionRepository();

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

        clientService.createClient(
                name1,
                cpf1,
                email1
        );

        UUID clientId =
                clientService.getClientId(cpf1);

        AccountIdentity account =
                accountService.createAccount(
                        clientId,
                        AccountType.SAVINGS
                );

        transactionService.deposit(
                account,
                Money.of("1000")
        );

        Clock february =
                Clock.fixed(
                        Instant.parse("2026-02-02T10:00:00Z"),
                        ZoneOffset.UTC
                );

        accountService =
                new AccountService(
                        accountRepository,
                        february
                );

        transactionService =
                new TransactionService(
                        accountService,
                        transactionRepository,
                        february
                );

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

    private void deposit(
            AccountIdentity account,
            String amount
    ) {

        transactionService.deposit(
                account,
                money(amount)
        );
    }

    private void withdraw(
            AccountIdentity account,
            String amount
    ) {

        transactionService.withdraw(
                account,
                money(amount)
        );
    }

    private void transfer(
            AccountIdentity from,
            AccountIdentity to,
            String amount
    ) {

        transactionService.transfer(
                from,
                to,
                money(amount)
        );
    }

    private Money balance(AccountIdentity account) {

        return transactionService
                .getAccountBalance(account);
    }

    private List<StatementData> history(
            AccountIdentity accountIdentity
    ) {

        return transactionService
                .getTransactionHistoryByAccountIdentity(accountIdentity);
    }

    private Money money(String value) {
        return Money.of(value);
    }

    private AccountIdentity createCheckingAccount() {

        return createAccount(
                cpf1,
                name1,
                email1,
                AccountType.CHECKING
        );
    }

    private AccountIdentity createSavingsAccount() {

        return createAccount(
                cpf2,
                name2,
                email2,
                AccountType.SAVINGS
        );
    }

    private AccountIdentity createAccount(
            Cpf cpf,
            PersonName name,
            Email email,
            AccountType type
    ) {

        clientService.createClient(
                name,
                cpf,
                email
        );

        UUID clientId = clientService.getClientId(cpf);

        return accountService.createAccount(
                clientId,
                type
        );
    }
}