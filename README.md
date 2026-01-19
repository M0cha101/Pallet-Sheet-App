# Pallet Sheet Generator

## Overview
This application was developed to digitize and automate the manual pallet sheet creation process at my door manufacturing job I designed this tool while working on the production floor to replace a time-consuming handwriting process.

The app uses **barcode scanning** to pull manufacturing order numbers, allowing for rapid data entry of door specifications and quantities per production line.

### The Problem
Previously, generating pallet sheets required manually cross-referencing stacks of paperwork. This was:
* **Time-Intensive:** Required manual lookup for every line item.
* **Error-Prone:** Hand-written specs were often illegible or miscounted.
* **Single-Point Failure:** A lost piece of paper meant lost data.

### The Solution
By digitizing the sheet, the app ensures that every manufacturing order (MO) is tracked accurately. Since each MO is unique to a customer order, the app prevents duplicates and ensures the quantities per line match the digital record.

---

## Features
* **Barcode Integration:** Scan Manufacturing Orders directly to retrieve order numbers.
* **Automated Data Entry:** Standardized forms for door dimensions and materials.
* **Line-Specific Tracking:** Seamlessly organize door quantities by their specific production line.
* **Accuracy Validation:** Built-in logic to ensure counts are accurate before finishing a sheet.
* **Time Savings:** Significantly reduces the time taken to generate a full sheet compared to the manual method.

---

## Built With
* **Language:** Java 17
* **Framework:** JavaFX (UI)
* **Build Tool:** Maven

---

## How to Run

### Prerequisites
* **Java JDK 17+**
* `JAVA_HOME` environment variable set to your JDK path.

### Execution
1. **Clone the repository:**
   ```powershell
   git clone [https://github.com/M0cha101/Pallet-Sheet-App.git](https://github.com/M0cha101/Pallet-Sheet-App.git)
   cd Pallet-Sheet-App
   
2. **./mvnw javafx:run**