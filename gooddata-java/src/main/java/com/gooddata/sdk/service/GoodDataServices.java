/*
 * Copyright (C) 2007-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service;

import com.gooddata.sdk.service.account.AccountService;
import com.gooddata.sdk.service.auditevent.AuditEventService;
import com.gooddata.sdk.service.connector.ConnectorService;
import com.gooddata.sdk.service.dataload.OutputStageService;
import com.gooddata.sdk.service.dataload.processes.ProcessService;
import com.gooddata.sdk.service.dataset.DatasetService;
import com.gooddata.sdk.service.executeafm.ExecuteAfmService;
import com.gooddata.sdk.service.export.ExportService;
import com.gooddata.sdk.service.featureflag.FeatureFlagService;
import com.gooddata.sdk.service.gdc.DataStoreService;
import com.gooddata.sdk.service.gdc.GdcService;
import com.gooddata.sdk.service.httpcomponents.SingleEndpointGoodDataRestProvider;
import com.gooddata.sdk.service.lcm.LcmService;
import com.gooddata.sdk.service.md.MetadataService;
import com.gooddata.sdk.service.md.maintenance.ExportImportService;
import com.gooddata.sdk.service.notification.NotificationService;
import com.gooddata.sdk.service.project.ProjectService;
import com.gooddata.sdk.service.project.model.ModelService;
import com.gooddata.sdk.service.projecttemplate.ProjectTemplateService;
import com.gooddata.sdk.service.warehouse.WarehouseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Configures services provided by {@link GoodData}
 */
class GoodDataServices {

    private final GoodDataRestProvider goodDataRestProvider;

    private static final Logger logger = LoggerFactory.getLogger(GoodDataServices.class);

    private final AccountService accountService;
    private final ProjectService projectService;
    private final MetadataService metadataService;
    private final ModelService modelService;
    private final GdcService gdcService;
    private final DataStoreService dataStoreService;
    private final DatasetService datasetService;
    private final ConnectorService connectorService;
    private final ProcessService processService;
    private final WarehouseService warehouseService;
    private final NotificationService notificationService;
    private final ExportImportService exportImportService;
    private final FeatureFlagService featureFlagService;
    private final OutputStageService outputStageService;
    private final ProjectTemplateService projectTemplateService;
    private final ExportService exportService;
    private final AuditEventService auditEventService;
    private final ExecuteAfmService executeAfmService;
    private final LcmService lcmService;

    GoodDataServices(final GoodDataRestProvider goodDataRestProvider) {
        this.goodDataRestProvider = goodDataRestProvider;

        accountService = new AccountService(getRestTemplate(), getSettings());
        projectService = new ProjectService(getRestTemplate(), accountService, getSettings());
        metadataService = new MetadataService(getRestTemplate(), getSettings());
        modelService = new ModelService(getRestTemplate(), getSettings());
        gdcService = new GdcService(getRestTemplate(), getSettings());
        exportService = new ExportService(getRestTemplate(), getSettings());
        warehouseService = new WarehouseService(getRestTemplate(), getSettings());
        connectorService = new ConnectorService(getRestTemplate(), projectService, getSettings());
        notificationService = new NotificationService(getRestTemplate(), getSettings());
        exportImportService = new ExportImportService(getRestTemplate(), getSettings());
        featureFlagService = new FeatureFlagService(getRestTemplate(), getSettings());
        outputStageService = new OutputStageService(getRestTemplate(), getSettings());
        projectTemplateService = new ProjectTemplateService(getRestTemplate(), getSettings());
        auditEventService = new AuditEventService(getRestTemplate(), accountService, getSettings());
        executeAfmService = new ExecuteAfmService(getRestTemplate(), getSettings());
        lcmService = new LcmService(getRestTemplate(), getSettings());

        dataStoreService = initDataStoreService(goodDataRestProvider, gdcService);

        datasetService = new DatasetService(getRestTemplate(), dataStoreService, getSettings());
        processService = new ProcessService(getRestTemplate(), accountService, dataStoreService, getSettings());
    }

    private static DataStoreService initDataStoreService(final GoodDataRestProvider goodDataRestProvider, final GdcService gdcService) {
        if (goodDataRestProvider instanceof SingleEndpointGoodDataRestProvider) {
            try {
                Class.forName("com.github.sardine.Sardine", false, GoodDataServices.class.getClassLoader());
                return new DataStoreService((SingleEndpointGoodDataRestProvider) goodDataRestProvider, gdcService);
            } catch (ClassNotFoundException e) {
                if (logger.isInfoEnabled()) {
                    logger.info("Optional dependency Sardine not found - WebDAV related operations are not supported");
                }
            }
        } else {
            if (logger.isInfoEnabled()) {
                logger.info("GoodDataRestProvider is not SingleEndpointGoodDataRestProvider - WebDAV related operations are not supported");
            }
        }

        return null;
    }

    RestTemplate getRestTemplate() {
        return goodDataRestProvider.getRestTemplate();
    }

    GoodDataSettings getSettings() {
        return goodDataRestProvider.getSettings();
    }

    ProjectService getProjectService() {
        return projectService;
    }

    AccountService getAccountService() {
        return accountService;
    }

    MetadataService getMetadataService() {
        return metadataService;
    }

    ModelService getModelService() {
        return modelService;
    }

    GdcService getGdcService() {
        return gdcService;
    }

    DataStoreService getDataStoreService() {
        return dataStoreService;
    }

    DatasetService getDatasetService() {
        return datasetService;
    }

    ExportService getExportService() {
        return exportService;
    }

    ProcessService getProcessService() {
        return processService;
    }

    WarehouseService getWarehouseService() {
        return warehouseService;
    }

    ConnectorService getConnectorService() {
        return connectorService;
    }

    NotificationService getNotificationService() {
        return notificationService;
    }

    ExportImportService getExportImportService() {
        return exportImportService;
    }

    FeatureFlagService getFeatureFlagService() {
        return featureFlagService;
    }

    OutputStageService getOutputStageService() {
        return outputStageService;
    }

    ProjectTemplateService getProjectTemplateService() {
        return projectTemplateService;
    }

    AuditEventService getAuditEventService() {
        return auditEventService;
    }

    ExecuteAfmService getExecuteAfmService() {
        return executeAfmService;
    }

    LcmService getLcmService() {
        return lcmService;
    }
}
