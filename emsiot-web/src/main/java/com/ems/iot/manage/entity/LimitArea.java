package com.ems.iot.manage.entity;

import java.util.Date;

public class LimitArea {
    private Integer limit_area_id;

    private String limit_area_name;

    private String station_ids;

    private Date add_time;

    private String black_list_elects;
    
    private Integer pro_id;
    
    private Integer city_id;
    
    private Integer area_id;

    public Integer getLimit_area_id() {
        return limit_area_id;
    }

    public void setLimit_area_id(Integer limit_area_id) {
        this.limit_area_id = limit_area_id;
    }

    public String getLimit_area_name() {
        return limit_area_name;
    }

    public void setLimit_area_name(String limit_area_name) {
        this.limit_area_name = limit_area_name == null ? null : limit_area_name.trim();
    }

    public String getStation_ids() {
        return station_ids;
    }

    public void setStation_ids(String station_ids) {
        this.station_ids = station_ids == null ? null : station_ids.trim();
    }

    public Date getAdd_time() {
        return add_time;
    }

    public void setAdd_time(Date add_time) {
        this.add_time = add_time;
    }

    public String getBlack_list_elects() {
        return black_list_elects;
    }

    public void setBlack_list_elects(String black_list_elects) {
        this.black_list_elects = black_list_elects == null ? null : black_list_elects.trim();
    }

	public Integer getPro_id() {
		return pro_id;
	}

	public void setPro_id(Integer pro_id) {
		this.pro_id = pro_id;
	}

	public Integer getCity_id() {
		return city_id;
	}

	public void setCity_id(Integer city_id) {
		this.city_id = city_id;
	}

	public Integer getArea_id() {
		return area_id;
	}

	public void setArea_id(Integer area_id) {
		this.area_id = area_id;
	}
    
    
}