// TODO

@ExtendWith(MockitoExtension.class)
class FlamsSynchronizeServiceTest {

    @Mock
    private FlamsSoapConnector flamsSoapConnector;

    @Mock
    private FlamsRequestBuilder requestBuilder;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper mapper;

    @InjectMocks
    private FlamsSynchronizeService service;

    private SynchronizedAccountDTO request;

    @BeforeEach
    void setUp() {
        request = SynchronizedAccountDTO.builder()
                .accountType(AccountType.NOSTRO)
                .goldenSource(GoldenSource.FIAMS)
                .build();
    }

    @Test
    void shouldSynchronizeAccountsSuccessfully() {

        // --- given ---
        List<AccountResponseDTO> soapResponse = List.of(
                new AccountResponseDTO(),
                new AccountResponseDTO()
        );

        List<Account> existingAccounts = List.of(
                new Account(), new Account()
        );

        List<Account> mappedAccounts = List.of(
                new Account(), new Account(), new Account()
        );

        when(flamsSoapConnector.process(
                anyString(),
                anyString(),
                anyString(),
                any()
        )).thenReturn(soapResponse);

        when(accountRepository.findAccountByEntityIdAndAccountTypeInAndCustomLabelIsNotNull(
                anyString(),
                anyList()
        )).thenReturn(existingAccounts);

        when(mapper.mapToEntity(
                eq(soapResponse),
                eq(request.getAccountType()),
                anyString(),
                eq(existingAccounts)
        )).thenReturn(mappedAccounts);

        // --- when ---
        SynchronizeResponseDTO response = service.synchronize(request);

        // --- then ---
        assertNotNull(response);
        assertEquals(3L, response.getCount());

        verify(flamsSoapConnector).process(
                anyString(),
                anyString(),
                anyString(),
                any()
        );

        verify(accountRepository).deleteAllByEntityIdAndAccountType(
                anyString(),
                eq(request.getAccountType())
        );

        verify(accountRepository).saveAll(mappedAccounts);
    }
}
