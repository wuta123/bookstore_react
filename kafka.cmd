@echo off
rem 打开一个新的cmd窗口，运行ZooKeeper，路径改为自己的路径
start cmd /k "C:\apache-zookeeper-3.7.1-bin\bin\zkServer.cmd"

rem 等待5秒
timeout /t 5 > nul

rem 删除Kafka日志文件，路径改为自己的路径
del /q C:\tmp\kafka-logs\*

rem 等待5秒
timeout /t 5 > nul

rem 在Kafka目录下启动Kafka服务器，路径改为自己的路径
cd C:\kafka_2.12-3.2.0
.\bin\windows\kafka-server-start.bat .\config\server.properties

rem 保持当前窗口打开
pause
