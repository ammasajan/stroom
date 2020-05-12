/*
 * This file is generated by jOOQ.
 */
package stroom.annotation.impl.db.jooq;


import javax.annotation.processing.Generated;

import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.UniqueKey;
import org.jooq.impl.Internal;

import stroom.annotation.impl.db.jooq.tables.Annotation;
import stroom.annotation.impl.db.jooq.tables.AnnotationDataLink;
import stroom.annotation.impl.db.jooq.tables.AnnotationEntry;
import stroom.annotation.impl.db.jooq.tables.records.AnnotationDataLinkRecord;
import stroom.annotation.impl.db.jooq.tables.records.AnnotationEntryRecord;
import stroom.annotation.impl.db.jooq.tables.records.AnnotationRecord;


/**
 * A class modelling foreign key relationships and constraints of tables of 
 * the <code>stroom</code> schema.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.3"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Keys {

    // -------------------------------------------------------------------------
    // IDENTITY definitions
    // -------------------------------------------------------------------------

    public static final Identity<AnnotationRecord, Long> IDENTITY_ANNOTATION = Identities0.IDENTITY_ANNOTATION;
    public static final Identity<AnnotationDataLinkRecord, Long> IDENTITY_ANNOTATION_DATA_LINK = Identities0.IDENTITY_ANNOTATION_DATA_LINK;
    public static final Identity<AnnotationEntryRecord, Long> IDENTITY_ANNOTATION_ENTRY = Identities0.IDENTITY_ANNOTATION_ENTRY;

    // -------------------------------------------------------------------------
    // UNIQUE and PRIMARY KEY definitions
    // -------------------------------------------------------------------------

    public static final UniqueKey<AnnotationRecord> KEY_ANNOTATION_PRIMARY = UniqueKeys0.KEY_ANNOTATION_PRIMARY;
    public static final UniqueKey<AnnotationDataLinkRecord> KEY_ANNOTATION_DATA_LINK_PRIMARY = UniqueKeys0.KEY_ANNOTATION_DATA_LINK_PRIMARY;
    public static final UniqueKey<AnnotationDataLinkRecord> KEY_ANNOTATION_DATA_LINK_FK_ANNOTATION_ID_STREAM_ID_EVENT_ID = UniqueKeys0.KEY_ANNOTATION_DATA_LINK_FK_ANNOTATION_ID_STREAM_ID_EVENT_ID;
    public static final UniqueKey<AnnotationEntryRecord> KEY_ANNOTATION_ENTRY_PRIMARY = UniqueKeys0.KEY_ANNOTATION_ENTRY_PRIMARY;

    // -------------------------------------------------------------------------
    // FOREIGN KEY definitions
    // -------------------------------------------------------------------------

    public static final ForeignKey<AnnotationDataLinkRecord, AnnotationRecord> ANNOTATION_DATA_LINK_FK_ANNOTATION_ID = ForeignKeys0.ANNOTATION_DATA_LINK_FK_ANNOTATION_ID;
    public static final ForeignKey<AnnotationEntryRecord, AnnotationRecord> ANNOTATION_ENTRY_FK_ANNOTATION_ID = ForeignKeys0.ANNOTATION_ENTRY_FK_ANNOTATION_ID;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Identities0 {
        public static Identity<AnnotationRecord, Long> IDENTITY_ANNOTATION = Internal.createIdentity(Annotation.ANNOTATION, Annotation.ANNOTATION.ID);
        public static Identity<AnnotationDataLinkRecord, Long> IDENTITY_ANNOTATION_DATA_LINK = Internal.createIdentity(AnnotationDataLink.ANNOTATION_DATA_LINK, AnnotationDataLink.ANNOTATION_DATA_LINK.ID);
        public static Identity<AnnotationEntryRecord, Long> IDENTITY_ANNOTATION_ENTRY = Internal.createIdentity(AnnotationEntry.ANNOTATION_ENTRY, AnnotationEntry.ANNOTATION_ENTRY.ID);
    }

    private static class UniqueKeys0 {
        public static final UniqueKey<AnnotationRecord> KEY_ANNOTATION_PRIMARY = Internal.createUniqueKey(Annotation.ANNOTATION, "KEY_annotation_PRIMARY", Annotation.ANNOTATION.ID);
        public static final UniqueKey<AnnotationDataLinkRecord> KEY_ANNOTATION_DATA_LINK_PRIMARY = Internal.createUniqueKey(AnnotationDataLink.ANNOTATION_DATA_LINK, "KEY_annotation_data_link_PRIMARY", AnnotationDataLink.ANNOTATION_DATA_LINK.ID);
        public static final UniqueKey<AnnotationDataLinkRecord> KEY_ANNOTATION_DATA_LINK_FK_ANNOTATION_ID_STREAM_ID_EVENT_ID = Internal.createUniqueKey(AnnotationDataLink.ANNOTATION_DATA_LINK, "KEY_annotation_data_link_fk_annotation_id_stream_id_event_id", AnnotationDataLink.ANNOTATION_DATA_LINK.FK_ANNOTATION_ID, AnnotationDataLink.ANNOTATION_DATA_LINK.STREAM_ID, AnnotationDataLink.ANNOTATION_DATA_LINK.EVENT_ID);
        public static final UniqueKey<AnnotationEntryRecord> KEY_ANNOTATION_ENTRY_PRIMARY = Internal.createUniqueKey(AnnotationEntry.ANNOTATION_ENTRY, "KEY_annotation_entry_PRIMARY", AnnotationEntry.ANNOTATION_ENTRY.ID);
    }

    private static class ForeignKeys0 {
        public static final ForeignKey<AnnotationDataLinkRecord, AnnotationRecord> ANNOTATION_DATA_LINK_FK_ANNOTATION_ID = Internal.createForeignKey(stroom.annotation.impl.db.jooq.Keys.KEY_ANNOTATION_PRIMARY, AnnotationDataLink.ANNOTATION_DATA_LINK, "annotation_data_link_fk_annotation_id", AnnotationDataLink.ANNOTATION_DATA_LINK.FK_ANNOTATION_ID);
        public static final ForeignKey<AnnotationEntryRecord, AnnotationRecord> ANNOTATION_ENTRY_FK_ANNOTATION_ID = Internal.createForeignKey(stroom.annotation.impl.db.jooq.Keys.KEY_ANNOTATION_PRIMARY, AnnotationEntry.ANNOTATION_ENTRY, "annotation_entry_fk_annotation_id", AnnotationEntry.ANNOTATION_ENTRY.FK_ANNOTATION_ID);
    }
}
