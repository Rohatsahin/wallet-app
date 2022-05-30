# Wallet Service

manage and store wallet amount changes

## Getting Started

service currently implemented inmemory but implementation interfaces is available so you change persistence system

### Dependencies

* database system is -> in memory
* code base -> kotlin and code structure is created by hexagonal arch
* coding with ddd approach and principles
* dependency management is controlled by maven

### Installing

* clone repository after run this maven scripts and build project

```
mvn clean verify
```

### Executing program

* project was cloned after open your existing ide and run this main class

```
com.walletapp.WalletAppApplication.kt
```

after you run the program open swagger page and test your rest api

http://localhost:8080/swagger-ui/index.html#/

## Author

rohatsahin92@gmail.com