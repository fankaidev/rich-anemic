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

CREATE TABLE `account` (
    `userId` INT NOT NULL PRIMARY KEY,
    `balance` INT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
