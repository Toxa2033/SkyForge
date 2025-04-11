package com.luigivampa92.remoteandroidbuilds.ideplugin.os

import com.github.markusbernhardt.proxy.util.PlatformUtil
import com.luigivampa92.remoteandroidbuilds.ideplugin.FileManager

class SshExecutorImpl(
    private val fileManager: FileManager,
    private val processListRetriever: ProcessListRetriever,
    private val processKiller: ProcessKiller
) : SshExecutor {

    private val shellExecutor = RcOnlyShellExecutor()

    override fun checkSshExists(): Boolean {
        val commandSshVersion = CMD_TEMPLATE_SSH_VERSION.format(getSshExecutableValueForPlatform())
        val result = shellExecutor.execute(commandSshVersion)
        return result.exitCode == 0
    }

    override fun checkSshConnection(sshAlias: String): Boolean {
        val commandSshConnectionTest = CMD_TEMPLATE_SSH_CHECK_CONNECTION.format(getSshExecutableValueForPlatform(), sshAlias)
        val result = shellExecutor.execute(commandSshConnectionTest)
        return result.exitCode == 0
    }

    override fun overridePropertiesOnServer(
        sshAlias: String, 
        sshUser: String, 
        projectDirName: String, 
        properties: String, 
        fileName: String
    ): Boolean {
        val workingDir = if ("root" == sshUser) {
            CMD_TEMPLATE_SSH_ROOT_WORKING_DIR.format(projectDirName)
        } else {
            CMD_TEMPLATE_SSH_WORKING_DIR.format(sshUser, projectDirName)
        }
        val sshAction = getSshExecutableValueForPlatform()
        val prettifyProp = properties.lines().joinToString(separator = "\\n")
        val command = "$sshAction $sshAlias mkdir -p $workingDir ; echo \"$prettifyProp\" > $workingDir/$fileName"

        val result = shellExecutor.execute(command)
        return result.exitCode == 0
    }

    override fun uploadDebugKeystoreToServer(sshAlias: String, user: String): Boolean {
        val androidDebugKeystoreFile = fileManager.androidDebugKeystoreFilePath
        if (!androidDebugKeystoreFile.isNullOrEmpty()) {
            val commandPrepareAndroidDebugKeystoreFolder = if ("root" == user) {
                CMD_TEMPLATE_SSH_PREPARE_DEBUG_KEYSTORE_FOLDER_USER_ROOT.format(getSshExecutableValueForPlatform(), sshAlias)
            } else {
                CMD_TEMPLATE_SSH_PREPARE_DEBUG_KEYSTORE_FOLDER_USER_NORMAL.format(getSshExecutableValueForPlatform(), sshAlias, user)
            }
            
            val prepareAndroidDebugKeystoreFolderResult = shellExecutor.execute(commandPrepareAndroidDebugKeystoreFolder, 15000)
            if (prepareAndroidDebugKeystoreFolderResult.exitCode != 0) {
                return false
            }
            
            val osAwareAndroidDebugKeystoreFile = FileManager.fixFilePathForWindowsCygwin(androidDebugKeystoreFile)
            val commandTransferAndroidKeystore = CMD_TEMPLATE_SCP_UPLOAD_DEBUG_KEYSTORE.format(getScpExecutableValueForPlatform(), osAwareAndroidDebugKeystoreFile, sshAlias)
            val result = shellExecutor.execute(commandTransferAndroidKeystore, 15000)
            return result.exitCode == 0
        } else {
            return false
        }
    }

    override fun startSshTunnelOnPort(sshAlias: String, port: Int): Boolean {
        if (port < 1 || port > 65535) {
            return false
        }
        val portValue = port.toString()
        val commandSshStartTunnel = CMD_TEMPLATE_SSH_START_TUNNEL.format(getSshExecutableValueForPlatform(), sshAlias, portValue, portValue)
        val result = shellExecutor.execute(commandSshStartTunnel)
        return result.exitCode == 0
    }

    override fun stopSshTunnelsOnPorts(ports: List<Int>): Boolean {
        return try {
            val sshTunnelProcesses = processListRetriever.getSshProcessesOnPorts(ports)
            for (process in sshTunnelProcesses) {
                processKiller.kill(process.pid, false)
            }
            true
        } catch (e: Throwable) {
            false
        }
    }

    override fun checkRsyncExists(): Boolean {
        val commandRsyncVersion = CMD_TEMPLATE_RSYNC_VERSION.format(getRsyncExecutableValueForPlatform())
        val result = shellExecutor.execute(commandRsyncVersion)
        return result.exitCode == 0
    }

    // on windows only cygwin or wsl binaries work as intended
    // default C:\\Windows\\System\\OpenSSH\\ssh.exe cannot create gateway ports and thus useless
    private fun getSshExecutableValueForPlatform(): String {
        val platform = PlatformUtil.getCurrentPlattform()
        return when (platform) {
            PlatformUtil.Platform.MAC_OS, PlatformUtil.Platform.LINUX -> "ssh"
            PlatformUtil.Platform.WIN -> "ssh"
            else -> throw RuntimeException("Current platform is not supported")
        }
    }

    private fun getScpExecutableValueForPlatform(): String {
        val platform = PlatformUtil.getCurrentPlattform()
        return when (platform) {
            PlatformUtil.Platform.MAC_OS, PlatformUtil.Platform.LINUX -> "scp"
            PlatformUtil.Platform.WIN -> "scp"
            else -> throw RuntimeException("Current platform is not supported")
        }
    }

    private fun getRsyncExecutableValueForPlatform(): String {
        val platform = PlatformUtil.getCurrentPlattform()
        return when (platform) {
            PlatformUtil.Platform.MAC_OS, PlatformUtil.Platform.LINUX -> "rsync"
            PlatformUtil.Platform.WIN -> "rsync"
            else -> throw RuntimeException("Current platform is not supported")
        }
    }

    companion object {
        const val DELIMETER = ";"
        const val CMD_TEMPLATE_SSH_WORKING_DIR = "/home/%s/.mirakle/%s/"
        const val CMD_TEMPLATE_SSH_ROOT_WORKING_DIR = "/root/.mirakle/%s/"
        const val CMD_TEMPLATE_SSH_VERSION = "%s -V"
        const val CMD_TEMPLATE_SSH_CHECK_CONNECTION = "%s -q -o StrictHostKeyChecking=no -o BatchMode=yes -o ConnectTimeout=5 %s exit"
        const val CMD_TEMPLATE_SSH_PREPARE_DEBUG_KEYSTORE_FOLDER_USER_NORMAL = "%s %s mkdir -p /home/%s/.android"
        const val CMD_TEMPLATE_SSH_PREPARE_DEBUG_KEYSTORE_FOLDER_USER_ROOT = "%s %s mkdir -p /root/.android"
        const val CMD_TEMPLATE_SCP_UPLOAD_DEBUG_KEYSTORE = "%s %s %s:~/.android/debug.keystore"
        const val CMD_TEMPLATE_SSH_START_TUNNEL = "%s -o ExitOnForwardFailure=yes -f -N %s -R %s:localhost:%s"
        const val CMD_TEMPLATE_RSYNC_VERSION = "%s --version"
    }
}