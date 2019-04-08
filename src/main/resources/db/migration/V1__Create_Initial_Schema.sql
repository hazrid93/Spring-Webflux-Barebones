
CREATE TABLE async_task
(
  id  integer  AUTO_INCREMENT PRIMARY KEY,
  il_attrib  MEDIUMTEXT NOT NULL,
  tdm_attrib   MEDIUMTEXT NOT NULL,
  task_start_date   DATETIME NOT NULL,
  task_compl_date   DATETIME NOT NULL,
  task_ack_date   DATETIME NOT NULL,
  metadata   MEDIUMTEXT NOT NULL,
  tdm_status   MEDIUMTEXT NOT NULL
);

INSERT INTO async_task (il_attrib, tdm_attrib, task_start_date, task_compl_date, task_ack_date, metadata, tdm_status) VALUES ('il_attrib_data1', 'tdm_attrib_data1', '2012-04-19 13:08:22', '1000-01-01 00:00:00', '1000-01-01 00:00:00', 'metadata_data1', 'tdm_status_data1');
INSERT INTO async_task (il_attrib, tdm_attrib, task_start_date, task_compl_date, task_ack_date, metadata, tdm_status) VALUES ('il_attrib_data2', 'tdm_attrib_data2', '2012-04-19 13:08:22', '1000-01-01 00:00:00', '1000-01-01 00:00:00', 'metadata_data1', 'tdm_status_data2');
INSERT INTO async_task (il_attrib, tdm_attrib, task_start_date, task_compl_date, task_ack_date, metadata, tdm_status) VALUES ('il_attrib_data3', 'tdm_attrib_data3', '2012-04-19 13:08:22', '1000-01-01 00:00:00', '1000-01-01 00:00:00', 'metadata_data1', 'tdm_status_data3');
INSERT INTO async_task (il_attrib, tdm_attrib, task_start_date, task_compl_date, task_ack_date, metadata, tdm_status) VALUES ('il_attrib_data4', 'tdm_attrib_data4', '2012-04-19 13:08:22', '1000-01-01 00:00:00', '1000-01-01 00:00:00', 'metadata_data1', 'tdm_status_data4');