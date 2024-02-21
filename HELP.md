# Getting Started

## Run application
`mvn compile spring-boot:run`

## Test data
You can add sample test data and test application via http://localhost:8080/swagger-ui/index.html#/

The simplest way to do that is to copy sql script from `src/test/resources/sql/add_test_data.sql` to
`src/main/resources/db/migration/V2000__add_test_data.sql`

## Info
Service calculates fee for every started hour (if not between free hours). 
For example if you enter at 6:45 you pay for time 6:00 to 6:59.

You won't be charged twice for same hour.
For example if you enter at 6:00, leave at 6:15 and enter again at 6:30 you won't be
charged again for that hour.

Entrance and exit doesn't have to be on the same day



