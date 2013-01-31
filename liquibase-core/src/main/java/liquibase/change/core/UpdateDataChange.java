package liquibase.change.core;

import liquibase.change.*;
import liquibase.database.Database;
import liquibase.statement.SqlStatement;
import liquibase.statement.UpdateExecutablePreparedStatement;
import liquibase.statement.core.UpdateStatement;
import liquibase.structure.core.Table;

import java.util.ArrayList;
import java.util.List;

@DatabaseChange(name = "update", description = "Update Data", priority = ChangeMetaData.PRIORITY_DEFAULT, appliesTo = "table")
public class UpdateDataChange extends AbstractModifyDataChange implements ChangeWithColumns<ColumnConfig> {

    private String catalogName;
    private String schemaName;
    private String tableName;
    private List<ColumnConfig> columns;

    @TextNode(nodeName="where")
    private String whereClause;

    public UpdateDataChange() {
        columns = new ArrayList<ColumnConfig>();
    }

    @DatabaseChangeProperty(requiredForDatabase = "all")
    public List<ColumnConfig> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnConfig> columns) {
        this.columns = columns;
    }

    public void addColumn(ColumnConfig column) {
        columns.add(column);
    }

    public void removeColumn(ColumnConfig column) {
        columns.remove(column);
    }

    public SqlStatement[] generateStatements(Database database) {

    	boolean needsPreparedStatement = false;
        for (ColumnConfig column : columns) {
            if (column.getValueBlob() != null) {
                needsPreparedStatement = true;
            }
            if (column.getValueClob() != null) {
                needsPreparedStatement = true;
            }
        }

        if (needsPreparedStatement) {
            return new SqlStatement[] { 
            		new UpdateExecutablePreparedStatement(database, catalogName, schemaName, tableName, columns)
            };
        }
    	
        UpdateStatement statement = new UpdateStatement(getCatalogName(), getSchemaName(), getTableName());

        for (ColumnConfig column : columns) {
            statement.addNewColumnValue(column.getName(), column.getValueObject());
        }

        statement.setWhereClause(whereClause);

        return new SqlStatement[]{
                statement
        };
    }

    public String getConfirmationMessage() {
        return "Data updated in " + getTableName();
    }

}
