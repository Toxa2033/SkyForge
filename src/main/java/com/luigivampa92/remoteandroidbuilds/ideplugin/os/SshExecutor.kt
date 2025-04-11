package com.luigivampa92.remoteandroidbuilds.ideplugin.os

interface SshExecutor {
    fun checkSshExists(): Boolean
    fun checkSshConnection(sshAlias: String): Boolean
    fun overridePropertiesOnServer(
        sshAlias: String,
        sshUser: String,
        projectDirName: String,
        properties: String,
        fileName: String
    ): Boolean

    fun uploadDebugKeystoreToServer(sshAlias: String, user: String): Boolean
    fun startSshTunnelOnPort(sshAlias: String, port: Int): Boolean
    fun stopSshTunnelsOnPorts(ports: List<Int>): Boolean
    fun checkRsyncExists(): Boolean
}