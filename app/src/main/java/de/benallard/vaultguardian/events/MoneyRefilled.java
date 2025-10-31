package de.benallard.vaultguardian.events;

public record MoneyRefilled(double amount) implements CashBoxEvent {}
