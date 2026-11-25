# Simple Inventory Tracker (Java DSA Project)

A console-based Java application for managing product inventory using `ArrayList`, linear search, sorting with `Comparator`, and persistent text file handling.

## Project Details
- **Project Title:** Simple Inventory Tracker
- **Course / Subject:** Object Oriented Programming with Java / Data Structures and Algorithms
- **Submitted By:** Gangitla Gayatri (ERP: RU-25-10504)
- **Guided By:** Mr. Suryamani Verma
- **Institution:** Rungta International Skills University, Bhilai, CG

## Features
- **Add Product:** Add new products with Product ID, Product Name, Quantity, and Price.
- **Display Products:** View all stored product records in a formatted table.
- **Search Product:** Linear search by Product ID or Product Name.
- **Update Product:** Modify product name, stock quantity, or price.
- **Delete Product:** Remove unwanted product records from the inventory.
- **Sort Products:** Sort records by Product ID, Product Name, or Quantity.
- **File Handling:** Automatically load existing records on startup and save records to `inventory.txt`.

## Data Structures & Time Complexity
- **Dynamic Storage (`ArrayList`):** Add — $O(1)$ amortized
- **Display:** $O(n)$
- **Searching (Linear Search):** $O(n)$
- **Update / Delete:** $O(n)$
- **Sorting (`Comparator`):** $O(n \log n)$
- **File Read / Write:** $O(n)$

## How to Compile and Run
```bash
javac main.java
java main
