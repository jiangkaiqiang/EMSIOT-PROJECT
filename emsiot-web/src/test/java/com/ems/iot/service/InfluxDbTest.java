package com.ems.iot.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.QueryResult;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;

import com.ems.iot.manage.util.InfluxDBConnection;
/**
 * 测试influxdb java api
 * @author barry
 *
 */
public class InfluxDbTest {
	public static void main(String[] args) {
		//testQuery();
		//testInsert();
		//testBatchPoints();
		testElectQuery();
		//testCountQuery();
		//testDelete();
	}
	public static void testElectQuery() {
		InfluxDBConnection influxDBConnection = new InfluxDBConnection("admin", "admin", "http://47.100.242.28:8086", "emsiot", null);
		/*QueryResult results = influxDBConnection count(DISTINCT(gua_card_num))
				.query("SELECT * FROM electStationTest2 order by time desc limit 1000");*/
		String strSql="select * from electStationTest3 " +
				//" where station_phy_num = '28954'"+
				 " where time >= '2019-03-13' and time < '2019-03-14' " +
			
				 "  GROUP BY gua_card_num ";
				/*" order by time desc ";*/
		
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date=new Date();
		Calendar calendar = Calendar.getInstance();
		System.err.println(sdf.format(new Date(1550136556930L)));
		/*try {
			
			date=sdf.parse("2019-01-28");
			calendar.setTime(date);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			System.err.println(sdf.format(calendar.getTime()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		QueryResult results = influxDBConnection
				.query(strSql);
		//results.getResults()是同时查询多条SQL语句的返回值，此处我们只有一条SQL，所以只取第一个结果集即可。
		Result oneResult = results.getResults().get(0);
		if (oneResult.getSeries() != null) {
			List<Series> series = oneResult.getSeries();
			List<String> list = series.get(0).getColumns();
			List<List<Object>> lists = series.get(0).getValues();
			System.err.println(list.indexOf("owner_name"));
			for (String str : list) {
				System.err.print(str+", ");
			}
			System.err.println();
			for (List<Object> list2 : lists) {
				for (int i = 0; i < list2.size(); i++) {
					System.err.print(list2.get(i)+", ");
				}
				System.err.println();
			}
			
			System.out.println(lists.size());
		}else {
			System.err.println(oneResult.getError());
		}
	}
	public static void testDelete() {
		InfluxDBConnection influxDBConnection = new InfluxDBConnection("admin", "admin", "http://47.100.242.28:8086", "emsiot", null);
		String results = influxDBConnection
				.deleteMeasurementData("DELETE FROM electStationTest3 ");
		//results.getResults()是同时查询多条SQL语句的返回值，此处我们只有一条SQL，所以只取第一个结果集即可。

		System.out.println(results);

	}
	public static void testQuery() {
		InfluxDBConnection influxDBConnection = new InfluxDBConnection("admin", "admin", "http://47.100.242.28:8086", "emsiot", null);
		QueryResult results = influxDBConnection
				.query("SELECT * FROM testbatch order by time desc limit 1000");
		//results.getResults()是同时查询多条SQL语句的返回值，此处我们只有一条SQL，所以只取第一个结果集即可。
		Result oneResult = results.getResults().get(0);
		if (oneResult.getSeries() != null) {
			List<Series> series = oneResult.getSeries();
			System.out.println(series.get(0));
		}	
	}
	public static void testInsert() {
		InfluxDBConnection influxDBConnection = new InfluxDBConnection("admin", "admin", "http://47.100.242.28:8086", "emsiot", null);
		Map<String, String> tags = new HashMap<String, String>();
		tags.put("tag1", "0.77");
		Map<String, Object> fields = new HashMap<String, Object>();
		fields.put("field1", "String类型");
		// 数值型，InfluxDB的字段类型，由第一天插入的值得类型决定
		fields.put("field2", 3.141592657);
		// 时间使用毫秒为单位
		influxDBConnection.insert("testjavaapi", tags, fields, System.currentTimeMillis(), TimeUnit.MILLISECONDS);
	}
	public static void testBatchPoints() {
		InfluxDBConnection influxDBConnection = new InfluxDBConnection("admin", "admin", "http://47.100.242.28:8086", "emsiot", null);
		Map<String, String> tags1 = new HashMap<String, String>();
		tags1.put("tag1", "0.99");
		Map<String, String> tags2 = new HashMap<String, String>();
		tags2.put("tag1", "0.98");
		Map<String, Object> fields1 = new HashMap<String, Object>();
		fields1.put("field1", "abc");
		// 数值型，InfluxDB的字段类型，由第一天插入的值得类型决定
		fields1.put("field1", 123456);
		Map<String, Object> fields2 = new HashMap<String, Object>();
		fields2.put("field2", "String类型");
		fields2.put("field2", 3.141592657);
		// 一条记录值
		Point point1 = influxDBConnection.pointBuilder("testbatch", System.currentTimeMillis(), tags1, fields1);
		Point point2 = influxDBConnection.pointBuilder("testbatch", System.currentTimeMillis(), tags2, fields2);
		BatchPoints batchPoints1 = BatchPoints.database("emsiot")
				.retentionPolicy(null).consistency(ConsistencyLevel.ALL).build();
		// 将两条记录添加到batchPoints中
		batchPoints1.point(point1);
		BatchPoints batchPoints2 = BatchPoints.database("db-test")
				.retentionPolicy(null).consistency(ConsistencyLevel.ALL).build();
		// 将两条记录添加到batchPoints中
		batchPoints2.point(point2);
		// 将不同的batchPoints序列化后，一次性写入数据库，提高写入速度
		List<String> records = new ArrayList<String>();
		records.add(batchPoints1.lineProtocol());
		records.add(batchPoints2.lineProtocol());
		// 将两条数据批量插入到数据库中
		influxDBConnection.batchInsert("emsiot", null, ConsistencyLevel.ALL, records);
	}
	public static void testCountQuery() {
		InfluxDBConnection influxDBConnection = new InfluxDBConnection("admin", "admin", "http://47.100.242.28:8086", "emsiot", null);
		QueryResult results = influxDBConnection
				.query("SELECT * FROM electStationTest3 order by time desc");
		//results.getResults()是同时查询多条SQL语句的返回值，此处我们只有一条SQL，所以只取第一个结果集即可。
		Result oneResult = results.getResults().get(0);
		if (oneResult.getSeries() != null) {
			List<Series> series = oneResult.getSeries();
			System.out.println(series.get(0).getValues().size());
		}	
		
	}
}
