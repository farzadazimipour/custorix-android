# Requirements Document

## Introduction

Custorix is a fintech-grade Personal Asset Ledger & Risk Analyzer for Android. It is a local-first application that enables users to track crypto and fiat assets, ingest real-time market data, perform risk exposure analysis, and simulate portfolio scenarios. The app models deterministic monetary calculations, immutable transaction history, and offline-first architecture patterns used in production fintech systems. Custorix targets Android devices and uses Jetpack Compose for its UI layer.

## Glossary

- **Custorix**: The Android application serving as a Personal Asset Ledger & Risk Analyzer.
- **Asset**: A financial instrument tracked by the user, either a Crypto_Asset or a Fiat_Asset.
- **Crypto_Asset**: A cryptocurrency asset such as BTC, ETH, or a Stablecoin.
- **Fiat_Asset**: A government-issued currency such as EUR or USD.
- **Stablecoin**: A Crypto_Asset pegged to a fiat currency (e.g., USDT, USDC, DAI).
- **Transaction**: An immutable record of a Buy, Sell, or Transfer operation on an Asset.
- **Transaction_Fee**: An explicit monetary cost associated with a Transaction, modeled separately from the Transaction amount.
- **Portfolio**: The collection of all Assets and their quantities owned by the user.
- **Ledger**: The append-only store of all Transactions, serving as the immutable history.
- **Market_Data_Service**: The component responsible for fetching spot prices, historical prices, and volatility metrics from external APIs (e.g., CoinGecko).
- **Local_Database**: The encrypted local persistence layer (Room or SQLDelight with SQLCipher) serving as the single source of truth.
- **Risk_Analyzer**: The component that computes portfolio allocation, volatility-weighted exposure, and scenario simulations.
- **Scenario_Simulator**: A feature within the Risk_Analyzer that models "what if" portfolio value changes based on hypothetical asset price drops.
- **Base_Currency**: The user-selected fiat currency used for displaying all monetary values (default: USD).
- **TTL_Cache**: A time-to-live cache layer that stores Market_Data_Service responses for a configurable duration.
- **Auth_Gate**: The app-level authentication screen requiring PIN or biometric verification before granting access.
- **Portfolio_Overview_Screen**: The home screen displaying total portfolio value, asset allocation, risk summary, and top assets.
- **Asset_Detail_Screen**: The screen displaying detailed information for a single Asset including price, holdings, allocation, price history chart, and transaction list.
- **Add_Transaction_Screen**: The screen for creating new Buy, Sell, or Transfer Transactions with validation.
- **Risk_Analysis_Screen**: The screen displaying portfolio volatility, risk breakdown, and the Scenario_Simulator.
- **Settings_Screen**: The optional screen for configuring Base_Currency, refresh interval, biometric lock, and data management.
- **Bottom_Navigation**: The primary navigation component with tabs for Portfolio_Overview_Screen, Risk_Analysis_Screen, and optionally Settings_Screen.

---

## Requirements

### Requirement 1: Asset Definition and Management

**User Story:** As a user, I want to define and manage crypto and fiat assets in my portfolio, so that I can track my holdings across multiple asset types.

#### Acceptance Criteria

1. THE Custorix SHALL support user-defined Assets of type Crypto_Asset (BTC, ETH, Stablecoins) and Fiat_Asset (EUR, USD).
2. THE Custorix SHALL represent all monetary values using BigDecimal with explicit decimal precision, not floating-point types.
3. WHEN a user adds a new Asset, THE Custorix SHALL persist the Asset to the Local_Database.
4. THE Custorix SHALL classify each Crypto_Asset as either a Stablecoin or a non-stablecoin Crypto_Asset.
5. THE Custorix SHALL compute each Asset holding quantity by aggregating all Transactions in the Ledger for that Asset.

### Requirement 2: Immutable Transaction Ledger

**User Story:** As a user, I want an immutable record of all my transactions, so that I have a reliable and auditable history of my asset activity.

#### Acceptance Criteria

1. THE Ledger SHALL store Transactions as append-only records that cannot be modified or deleted after creation.
2. WHEN a user creates a Transaction, THE Custorix SHALL record the Transaction type (Buy, Sell, or Transfer), the Asset, the amount, the price in Base_Currency, the optional Transaction_Fee, and the date.
3. THE Custorix SHALL model the Transaction_Fee as a separate explicit field on each Transaction.
4. WHEN a user creates a Buy Transaction, THE Custorix SHALL increase the Asset holding quantity by the Transaction amount.
5. WHEN a user creates a Sell Transaction, THE Custorix SHALL decrease the Asset holding quantity by the Transaction amount.
6. WHEN a user creates a Transfer Transaction, THE Custorix SHALL decrease the source Asset holding quantity and increase the destination Asset holding quantity by the Transaction amount, minus any Transaction_Fee.
7. FOR ALL valid Transactions, serializing a Transaction to JSON then parsing the JSON back SHALL produce an equivalent Transaction object (round-trip property).

### Requirement 3: Transaction Validation

**User Story:** As a user, I want the app to validate my transactions before recording them, so that my ledger remains consistent and free of invalid entries.

#### Acceptance Criteria

1. WHEN a user submits a Sell Transaction, THE Custorix SHALL reject the Transaction if the sell amount exceeds the current holding quantity for that Asset.
2. WHEN a user submits a Transaction with a negative amount, THE Custorix SHALL reject the Transaction and display a validation error.
3. WHEN a user submits a Transaction, THE Custorix SHALL enforce decimal precision matching the Asset type (e.g., 8 decimal places for BTC, 2 for fiat).
4. WHEN a user submits a Transaction with a Transaction_Fee, THE Custorix SHALL validate that the Transaction_Fee is non-negative and does not exceed the Transaction amount.
5. IF a Transaction fails validation, THEN THE Custorix SHALL display a specific error message describing the validation failure.

### Requirement 4: Market Data Ingestion

**User Story:** As a user, I want the app to fetch current and historical crypto prices, so that I can see accurate valuations of my portfolio.

#### Acceptance Criteria

1. THE Market_Data_Service SHALL fetch spot prices for all user-held Crypto_Assets from an external API (e.g., CoinGecko).
2. THE Market_Data_Service SHALL fetch historical price data for a configurable period (7 to 30 days) for each Crypto_Asset.
3. THE Market_Data_Service SHALL fetch volatility metrics for each Crypto_Asset.
4. WHEN the Market_Data_Service receives a successful API response, THE Market_Data_Service SHALL persist the fetched data to the Local_Database.
5. WHEN the Market_Data_Service receives a rate-limit response (HTTP 429), THE Market_Data_Service SHALL retry the request after the duration specified in the response header, or after a default backoff of 60 seconds.
6. THE TTL_Cache SHALL cache Market_Data_Service responses for a configurable duration (default: 5 minutes) and serve cached data for subsequent requests within the TTL window.
7. IF the Market_Data_Service fails to fetch data and the TTL_Cache has expired, THEN THE Market_Data_Service SHALL fall back to the last-known-good data stored in the Local_Database.
8. FOR ALL Market_Data_Service API responses, parsing the JSON response then serializing it back to JSON SHALL preserve all price and timestamp fields (round-trip property).

### Requirement 5: Offline-First Architecture

**User Story:** As a user, I want the app to work reliably without an internet connection, so that I can view and manage my portfolio at all times.

#### Acceptance Criteria

1. THE Local_Database SHALL serve as the single source of truth for all Asset, Transaction, and Market Data.
2. WHEN the Market_Data_Service fetches new data from the network, THE Custorix SHALL write the data to the Local_Database before the UI observes the update.
3. THE Custorix SHALL render all UI screens using data emitted from Local_Database reactive streams (Kotlin Flow).
4. WHILE the device has no network connectivity, THE Custorix SHALL display all portfolio data, transaction history, and the most recent market data from the Local_Database.
5. WHILE the device has no network connectivity, THE Custorix SHALL allow the user to create new Transactions and persist them to the Local_Database.
6. WHEN network connectivity is restored, THE Market_Data_Service SHALL automatically refresh market data from the external API.

### Requirement 6: Portfolio Overview

**User Story:** As a user, I want to see a summary of my entire portfolio on the home screen, so that I can quickly understand my financial position.

#### Acceptance Criteria

1. THE Portfolio_Overview_Screen SHALL display the total portfolio value in the user-selected Base_Currency.
2. THE Portfolio_Overview_Screen SHALL display the portfolio allocation as percentages broken down by Crypto_Asset, Stablecoin, and Fiat_Asset categories.
3. THE Portfolio_Overview_Screen SHALL display a risk summary sourced from the Risk_Analyzer.
4. THE Portfolio_Overview_Screen SHALL display a list of the user's top Assets sorted by value in descending order.
5. WHEN market data or transaction data changes in the Local_Database, THE Portfolio_Overview_Screen SHALL update the displayed values reactively.
6. THE Custorix SHALL compute the total portfolio value as the sum of (holding quantity × current spot price in Base_Currency) for each Asset, and the sum of all individual Asset values SHALL equal the displayed total (invariant property).

### Requirement 7: Asset Detail View

**User Story:** As a user, I want to view detailed information about a specific asset, so that I can understand its performance and my transaction history for it.

#### Acceptance Criteria

1. THE Asset_Detail_Screen SHALL display the current price, amount owned, total value in Base_Currency, and allocation percentage for the selected Asset.
2. THE Asset_Detail_Screen SHALL display a price history chart for the selected Asset covering a configurable period of 7 to 30 days.
3. THE Asset_Detail_Screen SHALL display the list of all Transactions from the Ledger for the selected Asset, ordered by date descending.
4. THE Asset_Detail_Screen SHALL provide a button to navigate to the Add_Transaction_Screen with the Asset pre-selected.
5. WHEN a user navigates to the Asset_Detail_Screen via a deep link, THE Custorix SHALL resolve the Asset identifier from the deep link URI and display the corresponding Asset detail.

### Requirement 8: Add Transaction Flow

**User Story:** As a user, I want to add buy, sell, and transfer transactions through a dedicated form, so that I can record my asset activity accurately.

#### Acceptance Criteria

1. THE Add_Transaction_Screen SHALL provide input fields for Transaction type (Buy, Sell, Transfer), Asset selector, amount, price in Base_Currency, optional Transaction_Fee, and date.
2. WHEN the user selects the Transfer Transaction type, THE Add_Transaction_Screen SHALL display a destination Asset selector field.
3. WHEN the user submits a valid Transaction, THE Custorix SHALL persist the Transaction to the Ledger and navigate back to the previous screen.
4. WHEN the user submits an invalid Transaction, THE Add_Transaction_Screen SHALL display inline validation errors without clearing the form inputs.
5. THE Add_Transaction_Screen SHALL enforce decimal precision in the amount and fee input fields matching the selected Asset type.

### Requirement 9: Risk and Exposure Analysis

**User Story:** As a user, I want to analyze the risk exposure of my portfolio, so that I can make informed decisions about my asset allocation.

#### Acceptance Criteria

1. THE Risk_Analyzer SHALL compute portfolio allocation percentages by Asset, by asset category (Crypto_Asset, Stablecoin, Fiat_Asset), and by risk tier (high-volatility vs stable).
2. THE Risk_Analyzer SHALL compute a volatility-weighted exposure score for the portfolio using volatility metrics from the Market_Data_Service.
3. THE Risk_Analysis_Screen SHALL display the portfolio volatility score, a risk breakdown chart (high-volatility vs stable assets), and the Scenario_Simulator.
4. THE Scenario_Simulator SHALL accept a percentage drop input via a slider ranging from -10% to -50%.
5. WHEN the user adjusts the Scenario_Simulator slider, THE Scenario_Simulator SHALL compute and display the projected portfolio value loss by applying the selected percentage drop to non-stablecoin Crypto_Assets only.
6. THE Risk_Analyzer SHALL compute the Stablecoin vs non-stablecoin risk split as a percentage of total portfolio value.
7. FOR ALL portfolio compositions, the sum of all individual Asset allocation percentages computed by the Risk_Analyzer SHALL equal 100% (invariant property).

### Requirement 10: Security and Privacy

**User Story:** As a user, I want my financial data to be encrypted and protected by authentication, so that my asset information remains private and secure.

#### Acceptance Criteria

1. THE Local_Database SHALL encrypt all stored data at rest using SQLCipher or an equivalent encryption library.
2. WHEN the user launches Custorix, THE Auth_Gate SHALL require PIN or biometric authentication before granting access to any app screen.
3. THE Custorix SHALL NOT include any third-party analytics SDKs or tracking libraries.
4. THE Custorix SHALL NOT transmit any user portfolio data, transaction data, or asset holdings to external servers.
5. WHEN the user requests to clear local data from the Settings_Screen, THE Custorix SHALL delete all data from the Local_Database and reset the app to its initial state.
6. THE Custorix SHALL NOT store external API keys in the application source code or in unencrypted local storage.

### Requirement 11: Navigation and Screen Architecture

**User Story:** As a user, I want intuitive navigation between app screens, so that I can move between portfolio, risk analysis, and settings efficiently.

#### Acceptance Criteria

1. THE Bottom_Navigation SHALL provide tabs for Portfolio_Overview_Screen and Risk_Analysis_Screen, with an optional tab for Settings_Screen.
2. THE Custorix SHALL implement navigation using Navigation 3 with multi-backstack support.
3. WHEN the user switches between Bottom_Navigation tabs, THE Custorix SHALL preserve the navigation state of each tab independently (multi-backstack).
4. THE Custorix SHALL support deep links to the Asset_Detail_Screen using a URI pattern containing the Asset identifier.
5. WHEN the Android system destroys and recreates the Activity, THE Custorix SHALL restore the full navigation state including the active tab and nested screen stack.
6. THE Custorix SHALL present the Add_Transaction_Screen as a nested or modal destination from the Asset_Detail_Screen.

### Requirement 12: Settings and Configuration

**User Story:** As a user, I want to configure app preferences such as base currency and security settings, so that the app behaves according to my needs.

#### Acceptance Criteria

1. THE Settings_Screen SHALL allow the user to select a Base_Currency from a list of supported fiat currencies.
2. THE Settings_Screen SHALL allow the user to configure the market data refresh interval for the Market_Data_Service.
3. THE Settings_Screen SHALL allow the user to enable or disable biometric authentication for the Auth_Gate.
4. THE Settings_Screen SHALL provide an option to clear all local data from the Local_Database.
5. WHEN the user changes the Base_Currency, THE Custorix SHALL recalculate and update all displayed monetary values across all screens using the new Base_Currency.

### Requirement 13: Performance and Reliability

**User Story:** As a user, I want the app to load quickly and handle errors gracefully, so that I have a smooth and dependable experience.

#### Acceptance Criteria

1. THE Portfolio_Overview_Screen SHALL render initial content from the Local_Database within 500 milliseconds of completing authentication.
2. WHILE the Market_Data_Service is fetching data from the network, THE Custorix SHALL display a non-blocking loading indicator without preventing user interaction.
3. IF the Market_Data_Service encounters a network error, THEN THE Custorix SHALL display an informational message to the user and continue operating with Local_Database data.
4. THE Custorix SHALL perform all database read and write operations on background threads, keeping the main UI thread unblocked.
5. IF the Local_Database encounters a read or write error, THEN THE Custorix SHALL log the error and display a user-facing error message.

### Requirement 14: Architecture and Testability

**User Story:** As a developer, I want the app to follow clean architecture with modular feature boundaries, so that the codebase is maintainable and testable.

#### Acceptance Criteria

1. THE Custorix SHALL organize code into feature modules (portfolio, asset, risk, settings) and core modules (domain, data, model, common) under the package namespace com.custorix.
2. THE Custorix SHALL implement unidirectional data flow where ViewModels expose UI state as immutable state objects and receive user actions as events.
3. THE Custorix SHALL use Hilt for dependency injection across all modules.
4. THE Custorix SHALL decouple domain logic (use cases, risk calculations, monetary operations) from the UI layer, enabling unit testing without Android framework dependencies.
5. THE Custorix SHALL provide repository interfaces in the domain layer with implementations in the data layer, enabling substitution with fakes in tests.

### Requirement 15: Accessibility

**User Story:** As a user with accessibility needs, I want the app to support assistive technologies, so that I can use all features of Custorix.

#### Acceptance Criteria

1. THE Custorix SHALL provide content descriptions for all interactive UI elements and informational icons in Jetpack Compose.
2. THE Custorix SHALL support dynamic text scaling for all text elements displayed on screen.
3. THE Custorix SHALL ensure sufficient color contrast ratios (minimum 4.5:1 for normal text, 3:1 for large text) for all text and interactive elements.
4. THE Custorix SHALL ensure all interactive elements have a minimum touch target size of 48dp × 48dp.
