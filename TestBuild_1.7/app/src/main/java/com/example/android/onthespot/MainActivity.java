package com.example.android.onthespot;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import android.graphics.Path;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static java.lang.Math.sin;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class MainActivity extends ActionBarActivity {
    //Declaration of all variables used by this activity.
    int newX, newY, typeChance, score, xPos, yPos, lastSpawn, xTouch, yTouch;
    int levelNumber, circleSize, rectangleSize, hexagonSize, circleSpawnChance, rectangleSpawnChance, hexagonSpawnChance, maximumShapes, spawnSpeed;
    float size, shapeSize, density, rotation;
    float circleSpeed, rectangleSpeed, hexagonSpeed;
    String newType, type;
    String backgroundColor, circleColor, rectangleColor, hexagonColor, circleBorderColor, rectangleBorderColor, hexagonBorderColor;
    boolean justTouched;
    List<MyView.Shape> shapes;
    Random rand;
    Paint paint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Initialization of all variables used by this activity.
        size = shapeSize = density = rotation = 0f;
        newX = newY = typeChance = xPos = yPos = lastSpawn = xTouch = yTouch = 0;
        score = 1000;
        newType = type = "";
        justTouched = false;
        shapes = new ArrayList<>();
        rand = new Random();
        paint = new Paint();

        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray jArray = obj.getJSONArray("main");
            JSONObject levelData = jArray.getJSONObject(getIntent().getExtras().getInt("level"));
            levelNumber = levelData.getInt("level");
            backgroundColor = levelData.getString("backgroundColor");
            circleColor = levelData.getString("circleColor");
            rectangleColor = levelData.getString("rectangleColor");
            hexagonColor = levelData.getString("hexagonColor");
            circleBorderColor = levelData.getString("circleBorderColor");
            rectangleBorderColor = levelData.getString("rectangleBorderColor");
            hexagonBorderColor = levelData.getString("hexagonBorderColor");
            maximumShapes = levelData.getInt("maximumShapes");
            spawnSpeed = levelData.getInt("spawnSpeed");
            circleSize = levelData.getInt("circleSize");
            rectangleSize = levelData.getInt("rectangleSize");
            hexagonSize = levelData.getInt("hexagonSize");
            circleSpeed = (float) levelData.getDouble("circleSpeed");
            rectangleSpeed = (float) levelData.getDouble("rectangleSpeed");
            hexagonSpeed = (float) levelData.getDouble("hexagonSpeed");
            circleSpawnChance = levelData.getInt("circleSpawnChance");
            rectangleSpawnChance = levelData.getInt("rectangleSpawnChance");
            hexagonSpawnChance = levelData.getInt("hexagonSpawnChance");
        }
        catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        super.onCreate(savedInstanceState);
        //Used to have the action bar of the application so it isn't overlayed on the screen during a fullscreen activity.
        getSupportActionBar().hide();
        setContentView(new MyView(this));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public String loadJSONFromAsset() throws IOException {
        String json;
        try {
            InputStream is = getAssets().open("leveldata.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        }

        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class MyView extends View {

        public MyView(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
        }

        @Override
        protected void onDraw(Canvas canvas) {
            KeyguardManager myKM = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            if (!myKM.inKeyguardRestrictedInputMode()) {

                super.onDraw(canvas);

                //Get the density of the screen if not done already
                //This cannot be done during OnCreate since the display is not initialized at that point.
                if (density == 0) {
                    density = getResources().getDisplayMetrics().density;
                }

                //This code makes sure that there won't spawn any more shapes than the maximum number allowed.
                //Also checks the variable lastSpawn so that shapes don't spawn too fast after each other.
                if (shapes.size() <= maximumShapes && lastSpawn > spawnSpeed) {
                    //Get a new random x and y coordinate used to spawn the new shape.
                    //The limits for this depend on the density and pixel height/width of the screen.
                    newX = (int) (rand.nextInt(getWidth() - Math.round(120 * density)) + (60 * density));
                    newY = (int) (rand.nextInt(getHeight() - Math.round(150 * density)) + (90 * density));
                    rotation = rand.nextInt(360);

                    //Get a random number between 0 and 100 used for the chance calculation of what shape will spawn.
                    typeChance = rand.nextInt(101);

                    //Depending on the level there is a specific chance percentage for what shape will spawn.
                    if (typeChance < circleSpawnChance) {
                        //Change the starting size of the shape depending on whether it's a circle, rectangle or hexagon.
                        size = circleSize * density;
                        //Make sure the new shape doesn't overlap an existing shape.
                        checkCollision();
                        newType = "Circle";
                    } else if (typeChance < (circleSpawnChance + rectangleSpawnChance)) {
                        //Change the starting size of the shape depending on whether it's a circle, rectangle or hexagon.
                        size = rectangleSize * density;
                        //Make sure the new shape doesn't overlap an existing shape.
                        checkCollision();
                        newType = "Rectangle";
                    } else {
                        //Change the starting size of the shape depending on whether it's a circle, rectangle or hexagon.
                        size = hexagonSize * density;
                        //Make sure the new shape doesn't overlap an existing shape.
                        checkCollision();
                        newType = "Hexagon";
                    }

                    //Make a new shape according to the Shape class we've created, give it all the data used to determine it's characteristics.
                    Shape newShape = new Shape(newX, newY, size, newType, rotation);
                    //Add the shape to our list of existing shapes.
                    shapes.add(newShape);
                    //Reset the spawn timer.
                    lastSpawn = 0;
                }

                else {
                    //Increment the timer if no new shape has spawned this frame.
                    lastSpawn++;
                }

                /****** Code for drawing background ********/
                paint.setColor(Color.parseColor(backgroundColor));
                paint.setAntiAlias(true);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawPaint(paint);

                //This for loop goes through all the shapes currently on the screen and draws them.
                //The code also checks whether a shape has been touched this frame and will delete it accordingly.
                for (int i = 0; i < shapes.size(); i++) {

                    //Get the coordinates, size and type of the current shape in the list.
                    xPos = shapes.get(i).getXPos();
                    yPos = shapes.get(i).getYPos();
                    shapeSize = shapes.get(i).getShapeSize();
                    type = shapes.get(i).getType();
                    rotation = shapes.get(i).getRotation();

                    //Depending on the type of the shape, call the right method and change the size of the shape.
                    switch (type) {
                        case "Circle":
                            drawCircle(canvas); //Call method to draw a circle.
                            shapeSize = shapeSize - (circleSpeed * density);
                            break;
                        case "Rectangle":
                            rotation = rotation + 1.5f;
                            drawRectangle(canvas); //Call method to draw a rectangle.
                            shapeSize = shapeSize - (rectangleSpeed * density);
                            shapes.get(i).setRotation(rotation);
                            break;
                        case "Hexagon":
                            rotation = rotation + 3f;
                            drawHexagon(canvas); //Call method to draw a hexagon.
                            shapeSize = shapeSize - (hexagonSpeed * density);
                            shapes.get(i).setRotation(rotation);
                            break;
                        default:
                            break;
                    }

                    //Save the updated size in our object.
                    shapes.get(i).setShapeSize(shapeSize);

                    //If the user has not touched the shape, remove it from our list and decrement i by 1.
                    if (shapeSize <= 0f) {
                        score = score - 50;
                        shapes.remove(i);
                        i--;
                    }

                    else {
                        //Check if there is a new touch this frame.
                        if (justTouched == true) {

                            //If this is the last shape in the list to be checked this frame,
                            //set justTouched on false so that there can be a new coordinate next frame.
                            if (shapes.size() - 1 == i) {
                                justTouched = false;
                            }

                            //Compare the coordinates of where the user touched the screen with the coordinates of the shape.
                            //If they overlap (meaning the user 'touched' the shape, remove the shape from the list and increase the score.
                            if (xTouch > (xPos - shapeSize - (5 * density)) &&
                                    xTouch < (xPos + shapeSize + (5 * density)) &&
                                    yTouch > (yPos - shapeSize - (5 * density)) &&
                                    yTouch < (yPos + shapeSize + (5 * density))) {
                                score = score + (int) shapeSize; //Depending on the size of the shape, give more or less points.
                                shapes.remove(i);
                                i--;
                                justTouched = false;
                            }
                        }
                    }
                }

                /****** Code for drawing debug text ********/
                paint.setColor(Color.BLACK);
                paint.setTextSize(12 * density);
                paint.setStrokeWidth(0);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("Resolution: " + getWidth() + "x" + getHeight() + "; " + newX + "," + newY + "; level: " + levelNumber + ", " + circleSize + "; Score: " + score + "; " + density + "; " + shapes.size(), getWidth() / 2, 25 * density, paint);
            }

            //This method makes sure that onDraw is called every frame.
            invalidate();

        }

        @Override
        public boolean onTouchEvent(MotionEvent e){
            if (e.getAction() == MotionEvent.ACTION_DOWN) {
                xTouch = (int) e.getX();
                yTouch = (int) e.getY();
                justTouched = true;
            }
            return true;
        }

        //Check collision is used when a new shape is spawned and 1 or more shapes already exist.
        //This code makes sure the new shape doesn't overlap an existing one.
        public void checkCollision() {
            //Make sure to check all the shapes on the screen for collisions.
            for (int i = 0; i < shapes.size(); i++) {
                //Get the coordinates and size of the current shape in the list.
                xPos = shapes.get(i).getXPos();
                yPos = shapes.get(i).getYPos();
                shapeSize = shapes.get(i).getShapeSize();

                //If the new random coordinates fall inside or near the existing one,
                //Create a new set of random coordinates and repeat the process until an empty location has been found.
                if (!(((newX + size) < (xPos - shapeSize - (5 * density))) ||
                        ((newX - size) > (xPos + shapeSize + (5 * density))) ||
                        ((newY + size) < (yPos - shapeSize - (5 * density))) ||
                        ((newY - size) > (yPos + shapeSize + (5 * density))))) {
                    i = -1; //Set i to -1 in order to reset the for loop and try again with the new coordinates.
                    newX = (int) (rand.nextInt(getWidth() - Math.round(120 * density)) + (60 * density));
                    newY = (int) (rand.nextInt(getHeight() - Math.round(150 * density)) + (90 * density));
                }
            }
        }

        /****** Code for drawing a circle ********/
        public void drawCircle(Canvas canvas) {
            //Draw the shape itself.
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.parseColor(circleColor));
            canvas.drawCircle(xPos, yPos, shapeSize, paint);

            //Draw the border around it.
            paint.setColor(Color.parseColor(circleBorderColor));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5 * density);
            canvas.drawCircle(xPos, yPos, shapeSize, paint);
        }

        /****** Code for drawing a rectangle ********/
        public void drawRectangle(Canvas canvas) {
            //Draw the shape itself.
            canvas.rotate(rotation, xPos, yPos);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.parseColor(rectangleColor));
            canvas.drawRect(xPos - shapeSize, yPos - shapeSize, xPos + shapeSize, yPos + shapeSize, paint);

            //Draw the border around it.
            paint.setColor(Color.parseColor(rectangleBorderColor));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5 * density);
            canvas.drawRect(xPos - shapeSize, yPos - shapeSize, xPos + shapeSize, yPos + shapeSize, paint);
            canvas.rotate(-rotation, xPos, yPos);
        }

        /****** Code for drawing a hexagon ********/
        public void drawHexagon(Canvas canvas) {
            //Created a path of lines to resemble the shape of a hexagon as there is no native method to do so.
            canvas.rotate(rotation, xPos, yPos);
            Path path = new Path();
            path.moveTo(xPos - (shapeSize / 2), yPos - ((float) sin(1.04719755) * shapeSize));
            path.lineTo(xPos + (shapeSize / 2), yPos - ((float) sin(1.04719755) * shapeSize));
            path.lineTo(xPos + shapeSize, yPos);
            path.lineTo(xPos + (shapeSize / 2), yPos + ((float) sin(1.04719755) * shapeSize));
            path.lineTo(xPos - (shapeSize / 2), yPos + ((float) sin(1.04719755) * shapeSize));
            path.lineTo(xPos - shapeSize, yPos);
            path.lineTo(xPos - (shapeSize / 2), yPos - ((float) sin(1.04719755) * shapeSize));
            path.close();

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.parseColor(hexagonColor));
            canvas.drawPath(path, paint);

            //Draw the border using this same path.
            paint.setColor(Color.parseColor(hexagonBorderColor));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(5 * density);
            canvas.drawPath(path, paint);
            canvas.rotate(-rotation, xPos, yPos);
        }

        //Class used for the 3 different shapes.
        public class Shape {
            private int xPos;
            private int yPos;
            private float shapeSize;
            private float rotation;
            private String type;


            public Shape(int xPos, int yPos, float shapeSize, String type, float rotation) {
                this.xPos = xPos;
                this.yPos = yPos;
                this.shapeSize = shapeSize;
                this.rotation = rotation;
                this.type = type;
            }

            public int getXPos() {
                return xPos;
            }

            public int getYPos() {
                return yPos;
            }

            public float getShapeSize() {
                return shapeSize;
            }

            public String getType() {
                return type;
            }

            public void setXPos(int xPos) {
                this.xPos = xPos;
            }

            public void setYPos(int yPos) {
                this.yPos = yPos;
            }

            public void setShapeSize(float shapeSize) {
                this.shapeSize = shapeSize;
            }

            public void setType(String type) {
                this.type = type;
            }

            public float getRotation() {
                return rotation;
            }

            public void setRotation(float rotation) {
                this.rotation = rotation;
            }
        }
    }
}
