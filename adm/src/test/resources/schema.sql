SET MODE MySQL;

CREATE TABLE `product` (
    `id` INT NOT NULL PRIMARY KEY,
    `price` INT NOT NULL,
    `quota` INT NOT NULL,
    `soldCount` INT NOT NULL,
    `status` VARCHAR(16) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `variant` (
    `id` INT NOT NULL PRIMARY KEY,
    `productId` INT NOT NULL,
    `quota` INT NOT NULL,
    `soldCount` INT NOT NULL,
    `status` VARCHAR(16) NOT NULL,
    KEY productId (`productId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `order` (
    `id` INT NOT NULL PRIMARY KEY,
    `userId` INT NOT NULL,
    `status` VARCHAR(16) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `order_item` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `orderId` INT NOT NULL,
    `productId` INT NOT NULL,
    `variantId` INT NOT NULL,
    `quantity` INT NOT NULL,
    KEY orderId (`orderId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `balance` (
    `userId` INT NOT NULL PRIMARY KEY,
    `amount` INT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO balance (userId, amount) VALUES (999, 10000);

CREATE TABLE `coupon` (
    `id` INT NOT NULL PRIMARY KEY,
    `userId` INT NOT NULL,
    `value` INT NOT NULL,
    `used` INT NOT NULL,
    KEY userId (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `payment` (
    `id` INT NOT NULL PRIMARY KEY,
    `userId` INT NOT NULL,
    `orderId` INT NOT NULL,
    `cashFee` INT NOT NULL,
    `couponId` INT NOT NULL,
    KEY payment_userId (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

