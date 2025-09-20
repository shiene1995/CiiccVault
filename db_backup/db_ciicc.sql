-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Sep 20, 2025 at 03:53 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `db_ciicc`
--

-- --------------------------------------------------------

--
-- Table structure for table `tb_account`
--

CREATE TABLE `tb_account` (
  `id` int(11) NOT NULL,
  `Fname` text NOT NULL,
  `Lname` text NOT NULL,
  `Username` text NOT NULL,
  `PasswordHash` text NOT NULL,
  `SaltHash` text NOT NULL,
  `account_number` int(50) NOT NULL,
  `balance_money` double(50,2) NOT NULL,
  `date_account_created` varchar(25) NOT NULL,
  `img_link` varchar(255) NOT NULL,
  `status` varchar(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tb_account`
--

INSERT INTO `tb_account` (`id`, `Fname`, `Lname`, `Username`, `PasswordHash`, `SaltHash`, `account_number`, `balance_money`, `date_account_created`, `img_link`, `status`) VALUES
(1, 'Kenneth', 'Salvador', 'admin', 'zvtTVcPcBi6bUNJC8yaMKKFcuNufaPN1UF4ur2rixrk=', 'rQ4ap4rJH2aocu2XuJrMUw==', 160920251, 197.01, '2025-09-16', '00916202501.jpg', 'true'),
(2, 'Sir', 'Marco', 'marco', '/UvO1Qi7XKfz0YFBtpuRPsI/EZOy8TJ8L69qE7Ka+iA=', 'I/WDlQ0oLOFrUPM6B+384Q==', 170920252, 104.02, '2025-09-17', '00170920252.jpg', 'true'),
(3, 'Master', 'Account', 'superadmin', 'cMLvXt2UfrbXEUXOnj6ibPjr+x12odOmQ3tQvHKNA28=', 'QhI9KgEqukmpuWBfCS3Sjg==', 999999999, 0.00, '2025-09-15', '', 'master');

-- --------------------------------------------------------

--
-- Table structure for table `tb_transactions`
--

CREATE TABLE `tb_transactions` (
  `id` int(11) NOT NULL,
  `userID` int(11) NOT NULL,
  `reference_number` int(11) NOT NULL,
  `transaction_type` varchar(50) NOT NULL,
  `sent_from_to_account` int(11) NOT NULL,
  `sent_date` varchar(25) NOT NULL,
  `sent_amount` double(50,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `tb_transactions`
--

INSERT INTO `tb_transactions` (`id`, `userID`, `reference_number`, `transaction_type`, `sent_from_to_account`, `sent_date`, `sent_amount`) VALUES
(1, 1, 290191137, 'Send Money', 170920252, '2025-09-18', 1.00),
(2, 2, 290191137, 'Received Money', 160920251, '2025-09-18', 1.00),
(3, 2, 910271744, 'Send Money', 160920251, '2025-09-18', 1.00),
(4, 1, 910271744, 'Received Money', 170920252, '2025-09-18', 1.00),
(5, 1, 698911639, 'Send Money', 170920252, '2025-09-18', 2.00),
(6, 2, 698911639, 'Received Money', 160920251, '2025-09-18', 2.00),
(7, 2, 899331173, 'Deposit Money', 999999999, '2025-09-19', 100.00),
(8, 1, 653146085, 'Deposit Money', 999999999, '2025-09-19', 100.00),
(9, 1, 676002614, 'Deposit Money', 999999999, '2025-09-19', 0.01),
(10, 2, 171117417, 'Deposit Money', 999999999, '2025-09-19', 1.02);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `tb_account`
--
ALTER TABLE `tb_account`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `tb_transactions`
--
ALTER TABLE `tb_transactions`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `tb_account`
--
ALTER TABLE `tb_account`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `tb_transactions`
--
ALTER TABLE `tb_transactions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
