/*
 * This file is generated by jOOQ.
*/
package stroom.streamstore.meta.impl.db.stroom.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;

import stroom.streamstore.meta.impl.db.stroom.Indexes;
import stroom.streamstore.meta.impl.db.stroom.Keys;
import stroom.streamstore.meta.impl.db.stroom.Stroom;
import stroom.streamstore.meta.impl.db.stroom.tables.records.StrmTypeRecord;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.1"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class StrmType extends TableImpl<StrmTypeRecord> {

    private static final long serialVersionUID = 701776887;

    /**
     * The reference instance of <code>stroom.STRM_TYPE</code>
     */
    public static final StrmType STRM_TYPE = new StrmType();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<StrmTypeRecord> getRecordType() {
        return StrmTypeRecord.class;
    }

    /**
     * The column <code>stroom.STRM_TYPE.ID</code>.
     */
    public final TableField<StrmTypeRecord, Integer> ID = createField("ID", org.jooq.impl.SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>stroom.STRM_TYPE.NAME</code>.
     */
    public final TableField<StrmTypeRecord, String> NAME = createField("NAME", org.jooq.impl.SQLDataType.VARCHAR(255).nullable(false), this, "");

    /**
     * Create a <code>stroom.STRM_TYPE</code> table reference
     */
    public StrmType() {
        this(DSL.name("STRM_TYPE"), null);
    }

    /**
     * Create an aliased <code>stroom.STRM_TYPE</code> table reference
     */
    public StrmType(String alias) {
        this(DSL.name(alias), STRM_TYPE);
    }

    /**
     * Create an aliased <code>stroom.STRM_TYPE</code> table reference
     */
    public StrmType(Name alias) {
        this(alias, STRM_TYPE);
    }

    private StrmType(Name alias, Table<StrmTypeRecord> aliased) {
        this(alias, aliased, null);
    }

    private StrmType(Name alias, Table<StrmTypeRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Stroom.STROOM;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.STRM_TYPE_NAME, Indexes.STRM_TYPE_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<StrmTypeRecord, Integer> getIdentity() {
        return Keys.IDENTITY_STRM_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<StrmTypeRecord> getPrimaryKey() {
        return Keys.KEY_STRM_TYPE_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<StrmTypeRecord>> getKeys() {
        return Arrays.<UniqueKey<StrmTypeRecord>>asList(Keys.KEY_STRM_TYPE_PRIMARY, Keys.KEY_STRM_TYPE_NAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StrmType as(String alias) {
        return new StrmType(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StrmType as(Name alias) {
        return new StrmType(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public StrmType rename(String name) {
        return new StrmType(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public StrmType rename(Name name) {
        return new StrmType(name, null);
    }
}
