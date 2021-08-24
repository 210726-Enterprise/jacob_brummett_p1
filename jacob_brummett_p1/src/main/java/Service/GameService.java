package Service;

import Logic.mORM;
import Model.Game;
import Util.Metamodel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;


public class GameService {
    private static final Logger logger = LoggerFactory.getLogger(GameService.class);
    private ObjectMapper mapper;
    private Metamodel<?> meta = new Metamodel(Game.class);

    //Constructor
    public GameService(){
        mapper = new ObjectMapper();

    }

    /**
     * postAGame method takes a servlet request and response as parameters
     * JSON code written in the body of the request is mapped to a new Game object
     * then the postGame method is called to abstract away ORM interaction
     * @param req
     * @param resp
     */
    public void postAGame(HttpServletRequest req, HttpServletResponse resp) {
        try {
            StringBuilder builder = new StringBuilder();
            req.getReader().lines()
                    .collect(Collectors.toList())
                    .forEach(builder::append);

            Game game = mapper.readValue(builder.toString(), Game.class);
            int result = postGame(game);

            if(result != 0){
                resp.setStatus(HttpServletResponse.SC_CREATED);
            } else{
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
            }

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            logger.warn(e.getMessage());
        }
    }


    /**
     * getAGame method takes a servlet request and response as parameters
     * JSON code written in the body of the request is mapped to a new Game object
     * the the getGame method is called to abstract away ORM interaction
     * @param req
     * @param resp
     */
    public void getAGame(HttpServletRequest req, HttpServletResponse resp) {
        StringBuilder builder = new StringBuilder();
        try {
            req.getReader().lines().forEach(s -> builder.append(s));
            Game game = mapper.readValue(builder.toString(), Game.class);
            String json = getGame(game.getGameID());
            if (json != null){
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getOutputStream().print(json);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * putAGame method takes a servlet request and response as parameters
     * parameters passed in the servlet request are parsed in order to determine
     * the coordinates of the cell to be updated, as well as its new value
     * then the putGame method is called to abstract away ORM interaction
     * @param req
     * @param resp
     */
    public void putAGame(HttpServletRequest req, HttpServletResponse resp) {
        StringBuilder builder = new StringBuilder();
            if (Integer.parseInt(req.getParameter("gameID")) != 0) {
                int result = putGame(Integer.parseInt(req.getParameter("gameID")), req.getParameter("columnName"), req.getParameter("newValue"));
                if (result != 0) {
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                } else {
                    resp.setStatus(HttpServletResponse.SC_CONFLICT);
                }
            }
    }


    /**
     * deleteAGame method takes a servlet request and response as parameters
     * a parameter passed in the servlet request is parsed in order to determine the row to be deleted
     * then the deleteGame method is called to abstract away ORM interaction
     * @param req
     * @param resp
     */
    public void deleteAGame(HttpServletRequest req, HttpServletResponse resp) {
        boolean result = deleteGame(Integer.parseInt(req.getParameter("gameID")));
        if(result){
            resp.setStatus(HttpServletResponse.SC_OK);
        } else{
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
        }
    }


    /**
     * getGame method calls the ORM to retrieve a specific game from the database,
     * then maps the game object to a string of json, which is returned
     * @param id the id of the game to be retrieved
     * @return returns a string formatted like json
     * @throws JsonProcessingException
     */
    private String getGame(int id) throws JsonProcessingException {
        return mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString((Game)mORM.selectRow(meta, id));
    }

    /**
     * postGame method calls the ORM to insert a game into the database
     * @param game the game to be inserted
     * @return returns an int signifying whether or not the insertion was successful
     */
    private int postGame(Game game){
        return mORM.insertRow(meta, game);

    }

    /**
     * putGame method calls the ORM to update a specific cell within the database
     * @param id the id of the game to be updated, 'y coordinate'
     * @param column the column in which the cell is stored, 'x coordinate'
     * @param newVal the updated value of the cell
     * @return returns an int signifying whether or not the update was successful
     */
    private int putGame(int id, String column, Object newVal){
        return mORM.updateCell(meta, column, newVal, id);
    }

    /**
     * deleteGame method calls the ORM to delete a game within the database
     * @param id the id of the game to be deleted
     * @return returns a boolean signifying whether or not the delete was successful
     */
    private boolean deleteGame(int id){
        return mORM.deleteRow(meta, id);
    }
}
