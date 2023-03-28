/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import dev.ikm.tinkar.common.service.CachingService;
import dev.ikm.tinkar.common.service.LoadDataFromFileController;
import dev.ikm.tinkar.entity.EntityService;
import dev.ikm.tinkar.entity.StampService;
import dev.ikm.tinkar.entity.load.LoadEntitiesFromFileController;

@SuppressWarnings("module")
        // 7 in HL7 is not a version reference
module dev.ikm.tinkar.entity {
    requires org.slf4j;
    requires io.activej.bytebuf;
    requires transitive dev.ikm.tinkar.component;
    requires dev.ikm.tinkar.dto;
    requires java.logging;
    requires transitive dev.ikm.tinkar.common;
    requires transitive com.github.benmanes.caffeine;
    requires org.eclipse.collections;
    requires org.eclipse.collections.api;
    requires static io.soabase.recordbuilder.core;
    requires static java.compiler;
    requires transitive dev.ikm.tinkar.terms;
    requires java.xml;
    requires tinkar.schema;
    requires com.google.protobuf;
    requires org.jgrapht.core;

    exports dev.ikm.tinkar.entity;
    exports dev.ikm.tinkar.entity.graph;
    exports dev.ikm.tinkar.entity.util;
    exports dev.ikm.tinkar.entity.load;
    exports dev.ikm.tinkar.entity.export;
    exports dev.ikm.tinkar.entity.transaction;
    exports dev.ikm.tinkar.entity.transfom;
    exports dev.ikm.tinkar.entity.graph.isomorphic;

    provides LoadDataFromFileController
            with LoadEntitiesFromFileController;

    uses CachingService;
    uses EntityService;
    uses StampService;
}
