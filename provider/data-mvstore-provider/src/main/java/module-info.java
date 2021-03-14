/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import org.hl7.tinkar.provider.mvstore.MVStoreProvider;
import org.hl7.tinkar.common.service.PrimitiveDataService;

@SuppressWarnings("module") // 7 in HL7 is not a version reference
module org.hl7.tinkar.provider.mvstore {
    requires java.base;
    requires org.eclipse.collections.api;
    requires org.hl7.tinkar.component;
    requires org.hl7.tinkar.common;
    requires com.google.auto.service;
    requires io.activej.bytebuf;
    requires org.hl7.tinkar.lombok.dto;
    requires org.apache.logging.log4j;
    requires org.hl7.tinkar.entity;
    requires jdk.unsupported;
    requires com.h2database.mvstore;
    provides PrimitiveDataService
            with MVStoreProvider;
}