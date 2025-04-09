package com.luigivampa92.remoteandroidbuilds.ideplugin.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.ui.components.JBScrollPane;
import com.luigivampa92.remoteandroidbuilds.ideplugin.ServiceLocator;
import com.luigivampa92.remoteandroidbuilds.ideplugin.services.RemoteBuildConfigurationService;
import com.luigivampa92.remoteandroidbuilds.ideplugin.services.RemoteBuildsConfiguration;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public final class RemoteBuildsConfigurationSettings implements Configurable {

    private Project project;
    private RemoteBuildConfigurationService configurationService = ServiceLocator.getInstance().getRemoteBuildConfigurationService();
    private RemoteBuildsConfigurationSettingsForm configurationForm;
    private RemoteBuildsConfiguration initialConfiguration;

    public RemoteBuildsConfigurationSettings() {}

    public RemoteBuildsConfigurationSettings(Project project) {
        this.project = project;
    }

    @Override
    public @Nullable JComponent createComponent() {
        configurationForm = new RemoteBuildsConfigurationSettingsForm();
        JPanel panel = configurationForm.getPanel();
        
        JBScrollPane scrollPane = new JBScrollPane(panel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        JPanel container = new JPanel(new VerticalFlowLayout(true, false));
        container.add(scrollPane);
        
        // Устанавливаем предпочтительный размер для контейнера
        container.setPreferredSize(new Dimension(600, 600));
        
        if (configurationService != null) {
            initialConfiguration = configurationService.getConfiguration();
            if (initialConfiguration != null) {
                configurationForm.setConfigurationState(initialConfiguration);
            }
        }
        
        // Центрируем панель настроек
        SwingUtilities.invokeLater(() -> {
            if (container.getParent() != null && container.getParent().getParent() != null) {
                Container topLevelContainer = container.getParent();
                while (topLevelContainer.getParent() != null) {
                    topLevelContainer = topLevelContainer.getParent();
                }
                
                if (topLevelContainer instanceof Window) {
                    Window window = (Window) topLevelContainer;
                    window.setLocationRelativeTo(null); // Центрирует окно относительно экрана
                }
            }
        });
        
        return container;
    }

    @Override
    public void reset() {
        // No need to load configuration here as it's already loaded in createComponent
    }

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "Remote Builds";
    }

    @Override
    public boolean isModified() {
        if (configurationForm != null) {
            RemoteBuildsConfiguration configurationState = configurationForm.getConfigurationState();
            return configurationState != null && !configurationState.equals(initialConfiguration);
        } else {
            return false;
        }
    }

    @Override
    public void apply() throws ConfigurationException {
        if (configurationService != null) {
            RemoteBuildsConfiguration configuration = configurationForm.getConfigurationState();
            if (configuration != null) {
                configurationService.saveConfiguration(configuration);
                initialConfiguration = configuration;
            }
        }
    }
}