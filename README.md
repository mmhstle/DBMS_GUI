# DBMS GUI Application

A simple Java Swing application for interacting with MySQL databases.

## Features

- **Login Page**: Prompts for MySQL username and password at startup instead of using default credentials.
- **Database Selection**: Automatically loads and displays available databases on startup. The first database is selected by default.
- **SQL Query Execution**: Enter and execute SQL queries in the text area.
- **Result Filtering**: Filter the query results by entering a search term.
- **Enhanced Table Display**:
  - **Larger display area** (1000x400) to accommodate long column names
  - **Auto-resize columns** to fit content when selected
  - **Click column header** to auto-resize that column
  - **All columns auto-resize** after query execution
- **Table Zoom & Navigation**: 
  - **Ctrl + Mouse Scroll** on results table: Zoom in/out (0.5x to 3.0x scale)
  - **Shift + Mouse Drag** on results table: Pan around zoomed content
  - **Horizontal & Vertical Scrollbars**: Always visible for easy navigation
- **Keyboard Shortcuts**:
  - **Enter** in query area: Execute query (and apply filter if filter field has text)
  - **Enter** in filter field: Execute query if filter is empty, or apply filter if has text
  - **Shift + Enter** in query area: Insert new line
- **Export Options**:
  - Export results to CSV file

## Prerequisites

- Java Development Kit (JDK) 8 or higher
- MySQL database server running on localhost:3306
- Valid MySQL username and password (entered at login)

## How to Run

1. Double-click `run_app.bat` in the project directory.
2. The application will automatically download the MySQL connector.
3. Enter your MySQL username and password in the login dialog.
4. The first available database will be selected automatically.
5. Enter your SQL query and press Enter or click "Execute Query".
6. **Columns auto-resize** automatically after query execution to fit content.
7. **Click any column header** to resize that specific column to fit its content.
8. Use the filter field and press Enter to filter results.
9. **Zoom the results table**: Hold Ctrl and scroll mouse wheel on the results table.
10. **Pan around zoomed content**: Hold Shift and drag on the results table.
11. Use the horizontal and vertical scrollbars to navigate large datasets.
12. Use the export button to save results to CSV.

## Project Structure

- `src/main/java/DBMSGuiApp.java` - Main application with GUI and database connectivity.
- `run_app.bat` - Batch file to compile and run the application.
- `pom.xml` - Maven configuration (optional, for IDE integration).

## Dependencies

- MySQL Connector/J 8.0.27
- Java Swing (included in JDK for GUI)

## Notes

- Ensure your MySQL server is running before starting the application.
- The filter functionality is client-side and works on the currently displayed results.
- For security, avoid using this application with production databases directly; use appropriate user permissions.


  https://github.com/mmhstle/DBMS_GUI
