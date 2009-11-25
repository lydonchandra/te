package com.hoskoo.porto;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Blob;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Portofolio {
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	
	@Persistent
	private String userid;
	
	@Persistent
	private Date buydate;
	
	@Persistent
	private String ticker;
	
	@Persistent
	private Float buyprice;

	@Persistent
	private Long quantity;
	
	@Persistent
	private Float high;
	
	@Persistent
	private Float low;
	
	@Persistent
	private Float close;
	
	@Persistent
	private String comment;

	@Persistent
	private Long volume;
	
	@Persistent
	private Blob chartimage;
	
	@Persistent
	private String charturl;
	
	public String getCharturl() {
		return "http://stockcharts.com/c-sc/sc?s=" + this.ticker + "&p=DAILY&b=5&g=0&i=0&r=3528";
	}

	public void setCharturl(String charturl) {
		this.charturl = charturl;
	}

	public Blob getChartImage() {
		return chartimage;
	}

	public void setchartImage(Blob chartimage) {
		this.chartimage = chartimage;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public Long getVolume() {
		return volume;
	}

	public void setVolume(Long volume) {
		this.volume = volume;
	}
	
	public Date getBuydate() {
		return buydate;
	}

	public void setBuydate(Date buydate) {
		this.buydate = buydate;
	}

	public Float getBuyprice() {
		return buyprice;
	}

	public void setBuyprice(Float buyprice) {
		this.buyprice = buyprice;
	}
	
	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Float getHigh() {
		return high;
	}

	public void setHigh(Float high) {
		this.high = high;
	}

	public Float getLow() {
		return low;
	}

	public void setLow(Float low) {
		this.low = low;
	}

	public Float getClose() {
		return close;
	}

	public void setClose(Float close) {
		this.close = close;
	}
	
	public Portofolio(String ticker, float buyprice, long quantity, float high, float low, String comment, String userid) throws MalformedURLException {
		this.ticker = ticker;
		this.buyprice = buyprice;
		this.quantity = quantity;
		this.high = high;
		this.low = low;
		this.comment = comment;
		this.userid = userid;
	}

	public String getTicker() {
		return ticker;
	}

	public void setTicker(String ticker) {
		this.ticker = ticker;
	}
	
	public int getGrade() {
		float grade = 0;
		if( this.getLow() != null && this.getHigh() != null && this.getBuyprice() != null ) {
			float range = this.getHigh() - this.getLow();
			float buyDiff = this.getBuyprice() - this.getLow();
			grade = buyDiff/range * 100;
		} 
		return (int)grade;
	}
}
