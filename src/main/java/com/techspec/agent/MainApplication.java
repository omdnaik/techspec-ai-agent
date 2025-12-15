//TODO
@ExtendWith(MockitoExtension.class)
class NYBossSynchronizeServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper mapper;

    @Mock
    private RestConnector connector;

    @InjectMocks
    private NYBossSynchronizeService service;

    @Test
    void shouldSynchronizeAccountsSuccessfully() {

        // GIVEN
        SynchronizedAccountDTO request = SynchronizedAccountDTO.builder()
                .goldenSource(GoldenSource.NY_BOSS)
                .build();

        List<Account> mappedAccounts = List.of(
                AccountTestData.account(1, AccountType.NOSTRO, "USD", "001"),
                AccountTestData.account(2, AccountType.VOSTRO, "EUR", "002"),
                AccountTestData.account(3, AccountType.CURRENT, "INR", "003")
        );

        when(connector.synchronize())
                .thenReturn(List.of()); // raw response is irrelevant

        when(mapper.mapResponseToAccounts(any()))
                .thenReturn(mappedAccounts);

        // WHEN
        SynchronizeResponseDTO response = service.synchronize(request);

        // THEN
        assertNotNull(response);
        assertEquals(3L, response.getCount());

        verify(mapper).mapResponseToAccounts(any());

        verify(accountRepository)
                .deleteAllByEntityIdIn(List.of(1L, 2L, 3L));

        verify(accountRepository)
                .saveAll(mappedAccounts);
    }
  @Test
void shouldThrowExceptionWhenConnectorFails() {

    when(connector.synchronize())
            .thenThrow(new RuntimeException("NYBoss down"));

    assertThrows(AccountSynchronizationException.class,
            () -> service.synchronize(
                    SynchronizedAccountDTO.builder()
                            .goldenSource(GoldenSource.NY_BOSS)
                            .build()
            ));
}
}

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class FiamsSynchronizeServiceIT {

    @Autowired
    private SynchronizeServiceFactory factory;

    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    private FlamsSoapConnector flamsSoapConnector;

    @Test
    void shouldSynchronizeFiamsAccounts_endToEnd() {

        // GIVEN
        SynchronizedAccountDTO request = SynchronizedAccountDTO.builder()
                .entityId(1L)
                .accountType(AccountType.NOSTRO)
                .goldenSource(GoldenSource.FIAMS)
                .build();

        List<AccountResponseDTO> soapResponse = List.of(
                new AccountResponseDTO(),
                new AccountResponseDTO()
        );

        when(flamsSoapConnector.process(any(), any(), any(), any()))
                .thenReturn(soapResponse);

        SynchronizeService service =
                factory.getService(GoldenSource.FIAMS);

        // WHEN
        SynchronizeResponseDTO response =
                service.synchronize(request);

        // THEN
        assertNotNull(response);
        assertEquals(2L, response.getCount());

        List<Account> savedAccounts =
                accountRepository.findAll();

        assertEquals(2, savedAccounts.size());

        verify(flamsSoapConnector, times(1))
                .process(any(), any(), any(), any());
    }
}

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class NYBossSynchronizeServiceIT {

    @Autowired
    private SynchronizeServiceFactory factory;

    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    private RestConnector restConnector;

    @Test
    void shouldSynchronizeNYBossAccounts_endToEnd() {

        // GIVEN
        SynchronizedAccountDTO request = SynchronizedAccountDTO.builder()
                .goldenSource(GoldenSource.NY_BOSS)
                .build();

        when(restConnector.synchronize())
                .thenReturn(List.of(
                        new Object(), new Object(), new Object()
                ));

        SynchronizeService service =
                factory.getService(GoldenSource.NY_BOSS);

        // WHEN
        SynchronizeResponseDTO response =
                service.synchronize(request);

        // THEN
        assertNotNull(response);
        assertEquals(3L, response.getCount());

        List<Account> savedAccounts =
                accountRepository.findAll();

        assertEquals(3, savedAccounts.size());

        verify(restConnector, times(1))
                .synchronize();
    }
}
