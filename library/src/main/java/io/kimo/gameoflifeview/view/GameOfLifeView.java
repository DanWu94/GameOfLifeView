package io.kimo.gameoflifeview.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.WindowManager;

import io.kimo.gameoflifeview.R;
import io.kimo.gameoflifeview.game.Cell;
import io.kimo.gameoflifeview.game.World;

/**
 * Custom surface view that displays each round of the game.
 */
public class GameOfLifeView extends SurfaceView implements Runnable {

    public static final int DEFAULT_PROPORTION = 50;
    public static final int DEFAULT_ALIVE_COLOR = Color.BLACK;
    public static final int DEFAULT_DEAD_COLOR = Color.WHITE;
    public static final boolean DEFAULT_CLEAR_MODE = false;


    private Thread thread;
    private boolean isRunning = false;

    private int columnWidth = 1;
    private int rowHeight = 1;
    private int numberOfColumns = 1;
    private int numberOfRows = 1;

    private World world;

    private int proportion = DEFAULT_PROPORTION;
    private int aliveColor = DEFAULT_ALIVE_COLOR;
    private int deadColor = DEFAULT_DEAD_COLOR;
    private boolean clearMode = DEFAULT_CLEAR_MODE;

    volatile boolean touched = false;
    volatile int touched_x, touched_y;
    volatile boolean win = false;

    public GameOfLifeView(Context context) {
        super(context);
        calculateWorldParams();
    }

    public GameOfLifeView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.game_of_life_view, 0, 0);
        ensureCorrectAttributes(a);

        calculateWorldParams();
    }

    public GameOfLifeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.game_of_life_view, defStyle, 0);
        ensureCorrectAttributes(a);

        calculateWorldParams();
    }

    @Override
    public void run() {

        while (isRunning) {
            if (!getHolder().getSurface().isValid())
                continue;

            Canvas canvas = getHolder().lockCanvas();
            world.rotate();
            getHolder().unlockCanvasAndPost(drawCells(canvas));
        }
    }

    public void start() {
        thread = null;
        isRunning = true;
        thread = new Thread(this);
        thread.start();
    }

    public void stop() {

        isRunning = false;
        while (true) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            break;
        }
    }

    public void reviveCellsAt(int x, int y) {

        int X = x;
        int Y = y;

        while(X >= world.getWidth())
            X--;

        while(Y >= world.getHeight())
            Y--;

        world.revive(X, Y);
    }

    public int getProportion() {
        return proportion;
    }

    public void setProportion(int proportion) {
        this.proportion = proportion;
        invalidate();
    }

    public int getAliveColor() {
        return aliveColor;
    }

    public void setAliveColor(int aliveColor) {
        this.aliveColor = aliveColor;
        invalidate();
    }

    public int getDeadColor() {
        return deadColor;
    }

    public void setDeadColor(int deadColor) {
        this.deadColor = deadColor;
        invalidate();
    }

    public int getNumberOfColumns() {
        return numberOfColumns;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    private void calculateWorldParams() {
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        Point point = new Point();
        display.getSize(point);

        numberOfColumns = point.x / proportion;
        numberOfRows = point.y / proportion;

        columnWidth = point.x / numberOfColumns;
        rowHeight = point.y / numberOfRows;

        world = new World(numberOfColumns, numberOfRows, true);
    }

    private Canvas drawCells(Canvas canvas) {

        for (Cell cell : world.getCells()) {
            Rect r = new Rect();
            Paint p = new Paint();

            if(cell.isAlive) {
                r.set((cell.x * columnWidth)-1, (cell.y * rowHeight)-1,
                        (cell.x * columnWidth + columnWidth)-1, (cell.y * rowHeight + rowHeight)-1);
                p.setColor(aliveColor);
            }
            else {
                r.set((cell.x * columnWidth)-1, (cell.y * rowHeight)-1,
                        (cell.x * columnWidth + columnWidth)-1, (cell.y * rowHeight + rowHeight)-1);
                p.setColor(deadColor);
            }
            canvas.drawRect(r, p);
        }

        Log.d("LiveCells", String.valueOf(world.getLiveCells().length));
        Log.d("ClearMode", String.valueOf(clearMode));
        if(world.getLiveCells().length==0 && clearMode){
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.BLACK);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(canvas.getWidth()/7);
            canvas.drawText("YOU WIN",
                    canvas.getWidth()/2,
                    canvas.getHeight()/2-canvas.getWidth()/14,
                    paint);
            paint.setTextSize(canvas.getWidth()/28);
            canvas.drawText("touch anywhere to enter next stage",
                    canvas.getWidth()/2,
                    canvas.getHeight()/2,
                    paint);
            isRunning = false;
            win = true;
        }

        return canvas;
    }

    private void ensureCorrectAttributes(TypedArray styles) {

        //ensuring proportion
        int styledProportion = styles.getInt(R.styleable.game_of_life_view_proportion, DEFAULT_PROPORTION);

        if(styledProportion > 0) {
            proportion = styledProportion;
        } else {
            throw new IllegalArgumentException("Proportion must be higher than 0.");
        }

        aliveColor = styles.getColor(R.styleable.game_of_life_view_aliveCellColor, DEFAULT_ALIVE_COLOR);
        deadColor = styles.getColor(R.styleable.game_of_life_view_deadCellColor, DEFAULT_DEAD_COLOR);
        clearMode = styles.getBoolean(R.styleable.game_of_life_view_clearMode, DEFAULT_CLEAR_MODE);

        styles.recycle();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        touched_x = (int)Math.floor(event.getX()/columnWidth);
        touched_y = (int)Math.floor(event.getY()/rowHeight);
        Log.d("TOUCH", Float.toString(touched_x)+", "+Float.toString(touched_y));

        int action = event.getAction();
        switch(action){
            case MotionEvent.ACTION_DOWN:
                touched = true;
                break;
            case MotionEvent.ACTION_MOVE:
                touched = true;
                break;
            case MotionEvent.ACTION_UP:
                touched = false;
                break;
            case MotionEvent.ACTION_CANCEL:
                touched = false;
                break;
            case MotionEvent.ACTION_OUTSIDE:
                touched = false;
                break;
            default:
        }
        if(touched){
            if(win){
                win = false;
                world = new World(numberOfColumns, numberOfRows, true);
                this.start();
            }
            Canvas canvas = getHolder().lockCanvas();
            world.revive(touched_x,touched_y);
            getHolder().unlockCanvasAndPost(drawCells(canvas));
        }
        return true; //processed
    }
}
