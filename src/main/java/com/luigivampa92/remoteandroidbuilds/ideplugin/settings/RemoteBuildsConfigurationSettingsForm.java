package com.luigivampa92.remoteandroidbuilds.ideplugin.settings;

import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UI;
import com.luigivampa92.remoteandroidbuilds.ideplugin.services.RemoteBuildsConfiguration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

public final class RemoteBuildsConfigurationSettingsForm {

    private final JPanel mainPanel;
    
    private final JBTextField sshAliasTextField;
    private final JBTextField sshUserNameTextField;
    private final JPasswordField sshUserPasswordTextField;
    private final JBCheckBox proxyRequiredCheckBox;
    private final JBTextField proxyPortTextField;
    private final JBLabel proxyPortLabel;
    private final JBLabel proxySettingsDescriptionLabel;
    private final JBCheckBox extraDependenciesRequiredCheckBox;
    private final JBTextField sdkDependenciesTextField;
    private final JBLabel sdkDependenciesLabel;
    private final JBLabel sdkDependenciesDescriptionLabel;

    public RemoteBuildsConfigurationSettingsForm() {
        // Создаем компоненты
        sshAliasTextField = new JBTextField();
        sshUserNameTextField = new JBTextField();
        sshUserPasswordTextField = new JPasswordField();
        proxyRequiredCheckBox = new JBCheckBox("Proxy required");
        proxyPortTextField = new JBTextField();
        proxyPortLabel = new JBLabel("Port:");
        proxySettingsDescriptionLabel = new JBLabel("<html><b>Note:</b> Using a proxy requires Nginx or other reverse-proxy server being set and properly configured on your local computer. This is currently not maintained by IDE, so you will have to configure it yourself. Check out plugin's GitHub page for more information</html>");
        extraDependenciesRequiredCheckBox = new JBCheckBox("Extra Android SDK dependencies required");
        sdkDependenciesTextField = new JBTextField();
        sdkDependenciesLabel = new JBLabel("Values:");
        sdkDependenciesDescriptionLabel = new JBLabel("<html><b>Note:</b> You probably do not need this. Normal Java/Kotlin android project should be built without any extra dependencies. Fill this field if your project uses something like NDK etc. Leave this blank if you are not certain. Check out plugin's GitHub page for more information</html>");

        // Настраиваем компоненты
        proxyPortLabel.setEnabled(false);
        proxySettingsDescriptionLabel.setEnabled(false);
        proxyPortTextField.setEnabled(false);
        
        sdkDependenciesLabel.setEnabled(false);
        sdkDependenciesDescriptionLabel.setEnabled(false);
        sdkDependenciesTextField.setEnabled(false);
        
        // Ограничиваем ширину текстовых полей
        sshAliasTextField.setColumns(20);
        sshUserNameTextField.setColumns(20);
        sshUserPasswordTextField.setColumns(20);
        proxyPortTextField.setColumns(20);
        sdkDependenciesTextField.setColumns(20);
        
        // Настройка информационных меток
        int width = 450;
        JPanel descriptionPanel1 = new JPanel(new BorderLayout());
        descriptionPanel1.add(proxySettingsDescriptionLabel, BorderLayout.CENTER);
        descriptionPanel1.setPreferredSize(new Dimension(width, 60));
        
        JPanel descriptionPanel2 = new JPanel(new BorderLayout());
        descriptionPanel2.add(sdkDependenciesDescriptionLabel, BorderLayout.CENTER);
        descriptionPanel2.setPreferredSize(new Dimension(width, 60));
        
        // Добавляем слушатели
        proxyRequiredCheckBox.addItemListener(e -> {
            boolean selected = e.getStateChange() == ItemEvent.SELECTED;
            setProxySettingsEnabled(selected);
        });
        
        extraDependenciesRequiredCheckBox.addItemListener(e -> {
            boolean selected = e.getStateChange() == ItemEvent.SELECTED;
            setSdkDependenciesSettingsEnabled(selected);
        });

        // Создаем панели
        JPanel sshPanel = createSshPanel();
        JPanel proxyPanel = createProxyPanel(descriptionPanel1);
        JPanel sdkPanel = createSdkPanel(descriptionPanel2);

        // Создаем основную панель
        mainPanel = FormBuilder.createFormBuilder()
                .addComponent(createSectionHeader("SSH Settings"))
                .addComponent(sshPanel)
                .addVerticalGap(JBUI.scale(16))
                .addComponent(createSectionHeader("Proxy Settings"))
                .addComponent(proxyPanel)
                .addVerticalGap(JBUI.scale(16))
                .addComponent(createSectionHeader("SDK Dependencies Settings"))
                .addComponent(sdkPanel)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
                
        mainPanel.setBorder(JBUI.Borders.empty(10));
        
        // Устанавливаем предпочтительный размер панели
        mainPanel.setPreferredSize(new Dimension(600, 600));
    }
    
    private JPanel createSectionHeader(String text) {
        JPanel panel = new JBPanel<>();
        panel.setBorder(JBUI.Borders.empty(5, 0));
        panel.add(new JLabel(text, JLabel.LEFT));
        return panel;
    }
    
    private JPanel createSshPanel() {
        FormBuilder builder = FormBuilder.createFormBuilder();
        
        builder.addLabeledComponent("Alias:", sshAliasTextField);
        builder.addLabeledComponent("Username:", sshUserNameTextField);
        builder.addLabeledComponent("Password:", sshUserPasswordTextField);
        
        return builder.getPanel();
    }
    
    private JPanel createProxyPanel(JPanel descriptionPanel) {
        FormBuilder builder = FormBuilder.createFormBuilder();
        
        builder.addComponent(proxyRequiredCheckBox);
        builder.addLabeledComponent(proxyPortLabel, proxyPortTextField);
        builder.addComponent(descriptionPanel);
        
        JPanel panel = builder.getPanel();
        panel.setBorder(JBUI.Borders.empty(5));
        return panel;
    }
    
    private JPanel createSdkPanel(JPanel descriptionPanel) {
        FormBuilder builder = FormBuilder.createFormBuilder();
        
        builder.addComponent(extraDependenciesRequiredCheckBox);
        builder.addLabeledComponent(sdkDependenciesLabel, sdkDependenciesTextField);
        builder.addComponent(descriptionPanel);
        
        JPanel panel = builder.getPanel();
        panel.setBorder(JBUI.Borders.empty(5));
        return panel;
    }

    public JPanel getPanel() {
        return mainPanel;
    }

    public void setConfigurationState(RemoteBuildsConfiguration state) {
        sshAliasTextField.setText(state.getSshAlias());
        sshUserNameTextField.setText(state.getSshUserName());
        sshUserPasswordTextField.setText(state.getSshUserPassword());
        
        boolean proxyRequired = state.isProxyRequired();
        proxyRequiredCheckBox.setSelected(proxyRequired);
        setProxySettingsEnabled(proxyRequired);
        proxyPortTextField.setText(state.getProxyPort());
        
        boolean extraDependenciesRequired = state.isExtraSdkDependenciesRequired();
        extraDependenciesRequiredCheckBox.setSelected(extraDependenciesRequired);
        setSdkDependenciesSettingsEnabled(extraDependenciesRequired);
        sdkDependenciesTextField.setText(state.getSdkDependencies());
    }

    public void resetConfigurationState() {
        sshAliasTextField.setText(null);
        sshUserNameTextField.setText(null);
        sshUserPasswordTextField.setText(null);
        proxyRequiredCheckBox.setSelected(false);
        setProxySettingsEnabled(false);
        proxyPortTextField.setText(null);
        extraDependenciesRequiredCheckBox.setSelected(false);
        setSdkDependenciesSettingsEnabled(false);
        sdkDependenciesTextField.setText(null);
    }

    public RemoteBuildsConfiguration getConfigurationState() {
        if (validateConfigState()) {
            return new RemoteBuildsConfiguration(
                    sshAliasTextField.getText(),
                    sshUserNameTextField.getText(),
                    new String(sshUserPasswordTextField.getPassword()),
                    proxyRequiredCheckBox.isSelected(),
                    proxyPortTextField.getText(),
                    extraDependenciesRequiredCheckBox.isSelected(),
                    sdkDependenciesTextField.getText()
            );
        } else {
            return null;
        }
    }

    private boolean validateConfigState() {
        return true; // todo !
    }

    private void setProxySettingsEnabled(boolean enabled) {
        proxyPortLabel.setEnabled(enabled);
        proxySettingsDescriptionLabel.setEnabled(enabled);
        proxyPortTextField.setEnabled(enabled);
    }

    private void setSdkDependenciesSettingsEnabled(boolean enabled) {
        sdkDependenciesLabel.setEnabled(enabled);
        sdkDependenciesDescriptionLabel.setEnabled(enabled);
        sdkDependenciesTextField.setEnabled(enabled);
    }
}