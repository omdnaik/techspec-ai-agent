//TODO
// --- method source ---
    static Stream<Arguments> synchronizationCases() {
        return Stream.of(
            Arguments.of(
                AccountType.NOSTRO,
                List.of(
                    AccountTestData.account(1, AccountType.NOSTRO, "USD", "OLD1"),
                    AccountTestData.account(1, AccountType.NOSTRO, "EUR", "OLD2")
                ),
                List.of(
                    AccountTestData.account(1, AccountType.NOSTRO, "USD", "NEW1"),
                    AccountTestData.account(1, AccountType.NOSTRO, "JPY", "NEW2")
                )
            ),
            Arguments.of(
                AccountType.VOSTRO,
                List.of(
                    AccountTestData.account(1, AccountType.VOSTRO, "INR", "OLD1")
                ),
                List.of(
                    AccountTestData.account(1, AccountType.VOSTRO, "INR", "NEW1"),
                    AccountTestData.account(1, AccountType.VOSTRO, "GBP", "NEW2"),
                    AccountTestData.account(1, AccountType.VOSTRO, "USD", "NEW3")
                )
            ),
            Arguments.of(
                AccountType.CURRENT,
                List.of(),
                List.of(
                    AccountTestData.account(1, AccountType.CURRENT, "EUR", "NEW1")
                )
            )
        );
    }
}
