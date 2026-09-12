# Import/Export Specification Document

## Project: Intelligent System for Analysis and Recommendations in Agriculture
## Team: 38

---

## 1. Overview

The import/export system allows users to:

- **Export** their agricultural data (crops, parcels, activities) to CSV or Excel format
- **Import** data from CSV files into the system
- Maintain data portability and backup capabilities
- Support bulk data operations

All operations are **user-specific** - each user can only access and modify their own data.

---

## 2. Supported File Formats

| Format | Extension | Encoding | Description |
|--------|-----------|----------|-------------|
| CSV | .csv | UTF-8 | Comma-separated values |
| Excel | .xlsx | UTF-8 | Microsoft Excel Open XML format |

### 2.1 CSV Format Specifications

- **Delimiter**: Comma (`,`)
- **Header**: First row contains column names (required)
- **Encoding**: UTF-8
- **Line ending**: CRLF or LF (both supported)
- **Empty values**: Allowed (represented as empty string)

### 2.2 Excel Format Specifications

- **Format**: XLSX (Office Open XML)
- **Sheets**: Multiple sheets (one per entity type)
- **Encoding**: UTF-8 internally

---

## 3. Export Functionality

### 3.1 Export Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/data/export/crops` | GET | Export crops to CSV |
| `/api/data/export/parcels` | GET | Export parcels to CSV |
| `/api/data/export/activities` | GET | Export activities to CSV |
| `/api/data/export/excel` | GET | Export all data to Excel |

**Parameters:**
- `userId` (required): The ID of the authenticated user

**Response:**
- Content-Type: `text/csv` or `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`
- Content-Disposition: `attachment; filename=<filename>`

### 3.2 Crops Export Structure

**File Name:** `crops.csv`

**CSV Structure:**
```csv
id,name,type,plantingDate
1,Wheat,Grain,2024-03-15
2,Corn,Grain,2024-04-01
3,Tomato,Vegetable,2024-05-10
