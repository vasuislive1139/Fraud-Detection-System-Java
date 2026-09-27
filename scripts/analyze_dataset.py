import csv
import sys
from collections import defaultdict

def analyze_csv(file_path):
    print(f"Analyzing {file_path}...")
    
    row_count = 0
    fraud_count = 0
    type_distribution = defaultdict(int)
    missing_values = defaultdict(int)
    headers = []
    
    with open(file_path, 'r', newline='') as f:
        reader = csv.reader(f)
        try:
            headers = next(reader)
        except StopIteration:
            print("File is empty.")
            return

        print(f"Schema: {headers}")
        
        try:
            is_fraud_idx = headers.index('isFraud')
            type_idx = headers.index('type')
        except ValueError as e:
            print(f"Missing expected column: {e}")
            return
            
        for row in reader:
            if not row:
                continue
            
            row_count += 1
            
            # Count missing values
            for i, val in enumerate(row):
                if not val.strip():
                    missing_values[headers[i]] += 1
                    
            # Track distribution
            tx_type = row[type_idx]
            type_distribution[tx_type] += 1
            
            # Track frauds
            if row[is_fraud_idx] == '1':
                fraud_count += 1
                
            if row_count % 1000000 == 0:
                print(f"Processed {row_count} rows...")
                
    print("\n--- ANALYSIS RESULTS ---")
    print(f"Total Rows (excluding header): {row_count:,}")
    print(f"Missing Values: {dict(missing_values) if missing_values else 'None detected'}")
    print("\nClass Imbalance (isFraud):")
    print(f"  Legitimate (0): {row_count - fraud_count:,}")
    print(f"  Fraudulent (1): {fraud_count:,}")
    print(f"  Fraud Ratio: {(fraud_count / row_count * 100) if row_count > 0 else 0:.4f}%")
    
    print("\nTransaction Type Distribution:")
    for tx_type, count in type_distribution.items():
        print(f"  {tx_type}: {count:,} ({count / row_count * 100:.2f}%)")
        
if __name__ == "__main__":
    analyze_csv("data/raw/PS_20174392719_1491204439457_log.csv")
