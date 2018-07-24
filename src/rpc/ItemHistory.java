package rpc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;

/**
 * Servlet implementation class ItemHistory
 */
@WebServlet(name = "history", urlPatterns = { "/history" })
public class ItemHistory extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ItemHistory() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 1. Parse HTTP POST request into JSON object
        // input format: {"user_id": "1111", "favorite": ["abcd", "efgh"]}
        JSONObject inputRequest = RpcHelper.parseHttpRequest(request);
        try {
            // 2. Convert JSONObject into Java data: get user and his/her favorite items
            String userId = inputRequest.getString("user_id");

            JSONArray favorite = inputRequest.getJSONArray("favorite");
            List<String> itemIds = new ArrayList<>();
            for (int i = 0; i < favorite.length(); i++) {
                itemIds.add(favorite.getString(i));
            }

            // 3. Save user and his favorite items into db
            DBConnection connection = DBConnectionFactory.getConnection();
            connection.setFavoriteItems(userId, itemIds);
            connection.close();

            RpcHelper.writeJSONObject(response, new JSONObject().put("result", "SUCCESS"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
	}

	/**
	 * @see HttpServlet#doDelete(HttpServletRequest, HttpServletResponse)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 1. Parse HTTP DELETE request into JSON object
        // input format: {"user_id": "1111", "favorite": ["abcd", "efgh"]}
        JSONObject inputRequest = RpcHelper.parseHttpRequest(request);
        try {
            // 2. Convert JSONObject into Java data: get user and his/her favorite items
            String userId = inputRequest.getString("user_id");

            JSONArray favorite = inputRequest.getJSONArray("favorite");
            List<String> itemIds = new ArrayList<>();
            for (int i = 0; i < favorite.length(); i++) {
                itemIds.add(favorite.getString(i));
            }

            // 3. Delete user and his favorite items from db
            DBConnection connection = DBConnectionFactory.getConnection();
            connection.unsetFavoriteItems(userId, itemIds);
            connection.close();

            RpcHelper.writeJSONObject(response, new JSONObject().put("result", "SUCCESS"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
	}

}
