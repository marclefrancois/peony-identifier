#!/usr/bin/env python3
import csv
import json
import sys

def convert_csv_to_json(csv_path, json_path):
    """Convert CSV to JSON with the same field names as Champ1PP.json"""
    
    data = []
    
    with open(csv_path, 'r', encoding='utf-8') as csvfile:
        # Skip the first 2 header rows
        next(csvfile)
        next(csvfile)
        
        reader = csv.reader(csvfile)
        
        for row in reader:
            if len(row) >= 9 and row[0] and row[0] != '':  # Skip empty rows
                # Map CSV columns to JSON field names (first 9 columns)
                entry = {
                    "champ": row[0] if row[0] else None,
                    "parcelle": row[1] if row[1] else None,
                    "rang": row[2] if row[2] else None,
                    "trou": row[3] if row[3] else None,
                    "variete": row[4] if row[4] else None,
                    "annee_plantation": row[5] if row[5] else None,
                    "taille": row[6] if row[6] else None,
                    "etiquette": row[7] if row[7] else None,
                    "vente": row[8] if row[8] else None
                }
                
                # Convert empty strings to null
                for key, value in entry.items():
                    if value == '' or value == 'na':
                        entry[key] = None
                
                data.append(entry)
    
    # Write JSON file
    with open(json_path, 'w', encoding='utf-8') as jsonfile:
        json.dump(data, jsonfile, indent=2, ensure_ascii=False)
    
    print(f"Converted {len(data)} entries from {csv_path} to {json_path}")

if __name__ == "__main__":
    # Convert Champ2PP.csv
    csv_path = "/Users/marclefrancois/Downloads/Champ2PP.csv"
    json_path = "/Users/marclefrancois/Documents/GIT/peony-identifier-claude/data/Champ2PP.json"
    
    convert_csv_to_json(csv_path, json_path)