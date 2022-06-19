package it.polito.tdp.yelp.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.yelp.model.Arco;
import it.polito.tdp.yelp.model.Business;
import it.polito.tdp.yelp.model.Review;
import it.polito.tdp.yelp.model.User;

public class YelpDao {

	public List<Business> getAllBusiness(){
		String sql = "SELECT * FROM Business";
		List<Business> result = new ArrayList<Business>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Business business = new Business(res.getString("business_id"), 
						res.getString("full_address"),
						res.getString("active"),
						res.getString("categories"),
						res.getString("city"),
						res.getInt("review_count"),
						res.getString("business_name"),
						res.getString("neighborhoods"),
						res.getDouble("latitude"),
						res.getDouble("longitude"),
						res.getString("state"),
						res.getDouble("stars"));
				result.add(business);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Review> getAllReviews(){
		String sql = "SELECT * FROM Reviews";
		List<Review> result = new ArrayList<Review>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Review review = new Review(res.getString("review_id"), 
						res.getString("business_id"),
						res.getString("user_id"),
						res.getDouble("stars"),
						res.getDate("review_date").toLocalDate(),
						res.getInt("votes_funny"),
						res.getInt("votes_useful"),
						res.getInt("votes_cool"),
						res.getString("review_text"));
				result.add(review);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<User> getAllUsers(){
		String sql = "SELECT * FROM Users";
		List<User> result = new ArrayList<User>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				User user = new User(res.getString("user_id"),
						res.getInt("votes_funny"),
						res.getInt("votes_useful"),
						res.getInt("votes_cool"),
						res.getString("name"),
						res.getDouble("average_stars"),
						res.getInt("review_count"));
				
				result.add(user);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<String> getCities(){
		String sql="SELECT DISTINCT city "
				+ "FROM business "
				+ "ORDER BY city";
		List<String> result= new ArrayList<>();
		Connection conn= DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs= st.executeQuery();
			while(rs.next()) {
				String city= rs.getString("city");
				result.add(city);
			}
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException();
		}
		return result;
	}
	
	public List<Business> getLocaliAnnoCitta(String citta, Year anno,Map<String,Business> idMap){
		String sql="SELECT * "
				+ "FROM business "
				+ "WHERE city = ? "
				+ "AND ( "
				+ "		SELECT COUNT(*) "
				+ "		FROM reviews "
				+ "		WHERE business.business_id= reviews.business_id "
				+ "		AND YEAR(review_date)= ? "
				+ "	)	>0 "
				+ "ORDER BY business_name;";
		List<Business> result= new ArrayList<>();
		Connection conn= DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, citta);
			st.setInt(2, anno.getValue());
			ResultSet res= st.executeQuery();
			while(res.next()) {
				Business business = new Business(res.getString("business_id"), 
						res.getString("full_address"),
						res.getString("active"),
						res.getString("categories"),
						res.getString("city"),
						res.getInt("review_count"),
						res.getString("business_name"),
						res.getString("neighborhoods"),
						res.getDouble("latitude"),
						res.getDouble("longitude"),
						res.getString("state"),
						res.getDouble("stars"));
				result.add(business);
				idMap.put(res.getString("business_id"), business);
			}
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("SQL ERROR");
		}
		return result;
	}
	
	
	public List<Arco> getArco(String citta,Year anno, Map<String,Business> idMap){
		String sql="SELECT b1.business_id AS id1,b2.business_id AS id2,AVG(r1.stars)-AVG(r2.stars) AS dif "
				+ "FROM business b1, business b2, reviews r1, reviews r2 "
				+ "WHERE b1.business_id=r1.business_id "
				+ "AND b2.business_id=r2.business_id "
				+ "AND b1.city=? "
				+ "AND b1.city= b2.city "
				+ "AND YEAR(r1.review_date)=? "
				+ "AND YEAR(r1.review_date)=YEAR(r2.review_date) "
				+ "AND b1.business_id<>b2.business_id "
				+ "GROUP BY b1.business_id, b2.business_id "
				+ "HAVING dif>0";
		List<Arco> result= new ArrayList<>();
		Connection conn= DBConnect.getConnection();
		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, citta);
			st.setInt(2, anno.getValue());
			ResultSet rs=st.executeQuery();
			while(rs.next()) {
				Business b1= idMap.get(rs.getString("id1"));
				Business b2= idMap.get(rs.getString("id2"));
				double peso= rs.getDouble("dif");
				Arco a = new Arco(b1,b2,peso);
				result.add(a);
			}
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("SQL ERROR");
		}
			return result;
	}
	
	
	
}
