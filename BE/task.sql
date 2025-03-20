DROP TABLE IF EXISTS task CASCADE;

CREATE TABLE task (
    taskId SERIAL,
    duedate TIMESTAMP NOT NULL,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    priority INT CHECK (priority BETWEEN 1 AND 5),
    status VARCHAR(20) NOT NULL,
    CONSTRAINT valid_status CHECK (status IN ('PENDING', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'))
) PARTITION BY RANGE (duedate);

-----Query các partition có trong task

SELECT inhrelid::regclass AS partition_name, inhparent::regclass AS parent_table
FROM pg_inherits WHERE inhparent = 'task'::regclass;
-----Query số row mỗi partition
SELECT tableoid::regclass AS partition_name, COUNT(*)
FROM task
GROUP BY tableoid;




------------------------------------------------------------------------
--index section::::

-----Tạo một triệu dòng dữ liệu để test 
INSERT INTO task (title, description, duedate, priority, status)
SELECT 
    'Task ' || i, 
    'Description for task ' || i,
    NOW() + (random() * INTERVAL '335 days'),
    FLOOR(random() * 5 + 1)::INT, 
    CASE FLOOR(random() * 4) 
        WHEN 0 THEN 'PENDING' 
        WHEN 1 THEN 'IN_PROGRESS' 
        WHEN 2 THEN 'COMPLETED' 
        ELSE 'CANCELLED' 
    END
FROM generate_series(1, 1000000) AS s(i);



---------------------
----Kiểm trả xem thời gian thực thi câu query so với lúc chưa có index
DROP INDEX IF EXISTS idx_task_status_duedate;
EXPLAIN ANALYZE 
SELECT * FROM task WHERE status = 'PENDING' AND dueDate >= '2025-03-01';

----Kiểm trả xem thời gian thực thi câu query so với lúc có index
--------create composite index
CREATE INDEX idx_task_status_duedate ON task(status, duedate);

EXPLAIN ANALYZE 
SELECT * FROM task WHERE status = 'PENDING' AND dueDate >= '2025-03-01';

