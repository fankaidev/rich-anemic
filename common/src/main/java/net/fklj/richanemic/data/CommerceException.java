package net.fklj.richanemic.data;

public abstract class CommerceException extends Exception {


    public static class CreateOrderException extends CommerceException {}

    public static class InvalidQuantityException extends CreateOrderException {}

    public static class VariantMismatchException extends CreateOrderException {}

    public static class DuplicateProductException extends CreateOrderException {}

    public static class ProductOutOfStockException extends CommerceException {}

    public static class InactiveProductException extends CommerceException {}

    public static class InactiveVariantException extends CommerceException {}

    public static class InvalidProductException extends CommerceException {}

    public static class InvalidVariantException extends CommerceException {}

    public static class VariantQuotaException extends CommerceException {}

    public static class OrderNotFoundException extends CommerceException {}

    public static class CouponNotFoundException extends CommerceException {}

    public static class CouponUsedException extends CommerceException {}

    public static class InvalidCouponException extends CommerceException {}

    public static class OrderPaidException extends CommerceException {}

    public static class InsufficientBalanceException extends CommerceException {}

    public static class InvalidBalanceAmountException extends CommerceException {}


}
