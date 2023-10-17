Task
======================
Есть папка с файлами. При помощи средств Apache Camel реализовать следующий процесс: файл достается из папка, далее, в зависимости от типа, выполняются некоторые действия.  
Если файл имеет расширение xml, то его содержимое необходимо отправить в брокер ActiveMQ.  
Если он имеет расширение txt, то его необходимо отправить в брокер, а так же записать в таблицу БД.  
Если расширение другое - выбрасывается исключение и отправлять файл в очередь invalid-queue в ActiveMQ.  
При обработке каждого сотого файла, отсылать письмо содержащее количество файлов txt, количество файлов xml, количество не распознанных файлов, а также время обработки пачки сообщений.
