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
    private final JTextArea localPropertiesTextArea;
    private final JTextArea gradlePropertiesTextArea;

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
        localPropertiesTextArea = new JTextArea();
        gradlePropertiesTextArea = new JTextArea();

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
        
        // Настраиваем размеры для всех полей
        Dimension textFieldSize = new Dimension(450, 30);
        sshAliasTextField.setPreferredSize(textFieldSize);
        sshUserNameTextField.setPreferredSize(textFieldSize);
        sshUserPasswordTextField.setPreferredSize(textFieldSize);
        proxyPortTextField.setPreferredSize(textFieldSize);
        sdkDependenciesTextField.setPreferredSize(textFieldSize);
        
        // Настраиваем размер и внешний вид текстовых областей
        localPropertiesTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        gradlePropertiesTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        localPropertiesTextArea.setLineWrap(true);
        gradlePropertiesTextArea.setLineWrap(true);
        localPropertiesTextArea.setWrapStyleWord(true);
        gradlePropertiesTextArea.setWrapStyleWord(true);
        
        // Добавляем различимый цвет фона для текстовых областей
        //localPropertiesTextArea.setBackground(UIManager.getColor("TextField.background"));
        //gradlePropertiesTextArea.setBackground(UIManager.getColor("TextField.background"));
        
        // Добавляем прокрутку для текстовых областей
        JScrollPane localPropertiesScrollPane = new JScrollPane(localPropertiesTextArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JScrollPane gradlePropertiesScrollPane = new JScrollPane(gradlePropertiesTextArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        Dimension textAreaSize = new Dimension(450, 150);
        localPropertiesScrollPane.setPreferredSize(textAreaSize);
        gradlePropertiesScrollPane.setPreferredSize(textAreaSize);

        localPropertiesScrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        gradlePropertiesScrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        //localPropertiesScrollPane.setBorder(JBUI.Borders.customLine(UIManager.getColor("TextField.borderColor")));
        //gradlePropertiesScrollPane.setBorder(JBUI.Borders.customLine(UIManager.getColor("TextField.borderColor")));
        
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
        
        // Создаем отдельные панели для текстовых областей с заголовками
        JPanel localPropertiesPanel = new JPanel(new BorderLayout());
        JLabel localPropertiesLabel = new JLabel("local.properties");
        //localPropertiesLabel.setFont(new Font(localPropertiesLabel.getFont().getName(), Font.BOLD, 16));
        localPropertiesLabel.setHorizontalAlignment(JLabel.LEFT);
        localPropertiesLabel.setForeground(UIManager.getColor("Label.foreground"));
        localPropertiesPanel.add(localPropertiesLabel, BorderLayout.NORTH);
        localPropertiesPanel.add(localPropertiesScrollPane, BorderLayout.CENTER);
        localPropertiesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel gradlePropertiesPanel = new JPanel(new BorderLayout());
        JLabel gradlePropertiesLabel = new JLabel("gradle.properties");
        //gradlePropertiesLabel.setFont(new Font(gradlePropertiesLabel.getFont().getName(), Font.BOLD, 16));
        gradlePropertiesLabel.setHorizontalAlignment(JLabel.LEFT);
        gradlePropertiesLabel.setForeground(UIManager.getColor("Label.foreground"));
        gradlePropertiesPanel.add(gradlePropertiesLabel, BorderLayout.NORTH);
        gradlePropertiesPanel.add(gradlePropertiesScrollPane, BorderLayout.CENTER);
        gradlePropertiesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Разделитель для отображения двух текстовых областей
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setPreferredSize(new Dimension(450, 2));
        JPanel separatorPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        separatorPanel.add(separator);

        // Создаем основную панель
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        
        // Добавляем все панели
        mainPanel.add(createSectionHeader("SSH Settings"));
        mainPanel.add(sshPanel);
        mainPanel.add(Box.createVerticalStrut(32));
        
        mainPanel.add(createSectionHeader("Proxy Settings"));
        mainPanel.add(proxyPanel);
        mainPanel.add(Box.createVerticalStrut(32));
        
        mainPanel.add(createSectionHeader("SDK Dependencies Settings"));
        mainPanel.add(sdkPanel);
        mainPanel.add(Box.createVerticalStrut(32));
        
        mainPanel.add(createSectionHeader("Properties Files"));
        mainPanel.add(localPropertiesPanel);
        mainPanel.add(separatorPanel);
        mainPanel.add(gradlePropertiesPanel);
        mainPanel.add(Box.createVerticalStrut(64));
        
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
    }
    
    private JPanel createSectionHeader(String text) {
        JPanel panel = new JBPanel<>(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel(text);
        label.setFont(new Font(label.getFont().getName(), Font.BOLD, 16));
        panel.add(label);
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
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(600, 600));
        scrollPane.setBorder(null);
        
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.add(scrollPane, BorderLayout.CENTER);
        return wrapperPanel;
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
        
        localPropertiesTextArea.setText(state.getLocalProperties());
        gradlePropertiesTextArea.setText(state.getGradleProperties());
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
        localPropertiesTextArea.setText(null);
        gradlePropertiesTextArea.setText(null);
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
                    sdkDependenciesTextField.getText(),
                    localPropertiesTextArea.getText(),
                    gradlePropertiesTextArea.getText()
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