package src.ui;


public class Table extends Board {

	public static final Font font = new Font("Arial", Font.PLAIN, 20);
	
	public final int X_LENGTH; // buffer between window edge and table
	public final int Y_LENGTH; // buffer between window edge and table
	
	private final JSlider delaySlider;
	private final JButton stopButton;
	
	private Block[] blocks; // array containing all the blocks in the table	
	private Block[] blank;
	private List<Integer> coinBag;

    private int tableSize;
    
    private ArrayList<Integer> road;
    private Integer entrance;
    private Integer exit;

    private String message;

	private BufferedImage coin;
	// private JFrame jframe;

	/* Constructor ---------------------------------------------------------------------------------------------------------------------*/
	
	private static Table instance;
	public static Table instance(String filename) {
		if(instance == null)
			if(filename.isEmpty())
				instance = new Table(filename);
			else
				throw new InstantiationError("Instance needs a filename");

		return instance;
	}
	public static Table newInstance(String filename) {
		return instance = new Table(filename);
	}

	private Table(String filename) {
		super(filename);

		X_LENGTH 	= super.getXLength();
		Y_LENGTH 	= super.getyLength();

		tableSize 	= super.getTableSize();
		
		inputLayer 	= super.getInputLayer();
		hidden 		= super.getHidden();
		output 		= super.getOutput();
		blocks 		= super.getBlocks();
		
		coin 		= super.getCoinImage();
	}


	/*----------------------------------------------------------------------------------------------------------------------------------*/

	private Block[] copyArray(Block[] blocks) {
		return Arrays
					.stream(blocks)
					.map(Block::copyOf)
             		.toArray(Block[]::new);
	}

	public Block[] setBlocks(Block[] plain) {
		return blocks = plain;
	}

	public Block[] getBlocks() {
		return copyArray(blocks);
	}
	
	public Block[] getBlank() {
		return copyArray(blank);
	}

	public void clear() {
		blocks = copyArray(blank);
	}

	/*----------------------------------------------------------------------------------------------------------------------------------*/
	
	private Integer move(Color color, int pos, boolean refresh) {
		if(isWall(pos) == true) {
			try {
				if(refresh)
					setWallSpot(Color.RED, pos);
				else
					setWallSpotWR(Color.RED, pos);
			} catch(Exception e) { e.printStackTrace(); }
			return pos;
		}

		try{
			if(refresh)
				setSpot(color, pos);
			else
				setSpotWR(color, pos);
		} catch(Exception e) { e.printStackTrace(); }
		return pos;
	}

	/*
	 *	Move with Blocking positions (returning null) if is a wall
	 */
	public Integer moveUpBPos(Color color, int cur, boolean refresh) {
		return validateWallMoviment(moveUpPos(color, cur, refresh));
	}
	public Integer moveDownBPos(Color color, int cur, boolean refresh) {
		return validateWallMoviment(moveDownPos(color, cur, refresh));
	}
	public Integer moveLeftBPos(Color color, int cur, boolean refresh) {
		return validateWallMoviment(moveLeftPos(color, cur, refresh));
	}
	public Integer moveRightBPos(Color color, int cur, boolean refresh) {
		return validateWallMoviment(moveRightPos(color, cur, refresh));
	}

	private Integer validateWallMoviment(Integer pos) {
		if(pos == null || isWall(pos)) return null;
		return pos;
	}

	private Integer moveUpPos(Color color, int cur, boolean refresh) {
		int pos = cur - X_LENGTH;
		if( pos < 0 ) return null;
		return move(color, pos, refresh);
	}
	private Integer moveDownPos(Color color, int cur, boolean refresh) {
		int pos = cur + X_LENGTH;
		if( pos >= tableSize ) return null;
		return move(color, pos, refresh);
	}
	private Integer moveLeftPos(Color color, int cur, boolean refresh) {
		int pos = cur - 1;
		if( (pos/X_LENGTH) != (cur/X_LENGTH) || pos < 0 ) return null;
		return move(color, pos, refresh);
	}
	private Integer moveRightPos(Color color, int cur, boolean refresh) {
		int pos = cur + 1;
		if( (pos/X_LENGTH) != (cur/X_LENGTH) || pos >= tableSize ) return null;
		return move(color, pos, refresh);
	}

	public double nearestObjective(int pos, int manhattan) {
		// int nextCoin = nearestCoin(pos);
		int nextCoin = Integer.MAX_VALUE;
		if(nextCoin < manhattan)
			return nextCoin;

		return manhattan;
	}

	public int nearestCoin(int pos) {
		double[] arnd;
		int result = Integer.MAX_VALUE;

		for(
			int i = 3;
				((i+pos) < tableSize && (i-pos) > 0) ||
				result != Integer.MAX_VALUE;
			i += 3
		) {
			if((i-pos) > 0)
				result = catchCoinPos(pos-i);
			if((i+pos) < tableSize && result != Integer.MAX_VALUE)
				result = catchCoinPos(pos+i);
		}

		return result;
	}

	public int catchCoinPos(int pos) {
		double[] around = lookAround(pos);
		if (Arrays.binarySearch(around, (double) Constants.COIN) >= 0) {
			if(around[Constants.NORTH] == Constants.COIN) 	return getUpPos(pos);
			if(around[Constants.SOUTH] == Constants.COIN) 	return getDownPos(pos);
			if(around[Constants.WEST] == Constants.COIN)	return getLeftPos(pos);
			if(around[Constants.EAST] == Constants.COIN)	return getRightPos(pos);
		}
		return Integer.MAX_VALUE;
	}

	public double[] lookAround(int pos) {
		double[] around = new double[Constants.ENTRIES];
		int posu = pos - X_LENGTH;
		int posd = pos + X_LENGTH;
		int posl = pos - 1;
		int posr = pos + 1;
		around[Constants.NORTH] = lookUp(posu, posu < 0);
		around[Constants.SOUTH] = lookUp(posd, posd >= tableSize);
		around[Constants.WEST] 	= lookUp(posl, (posl/X_LENGTH) != (pos/X_LENGTH) || posl < 0);
		around[Constants.EAST] 	= lookUp(posr, (posr/X_LENGTH) != (pos/X_LENGTH) || posr >= tableSize);

		return around;
	}

	public double lookUp(int pos, boolean onboard) {
		if 		(  onboard  )	return Constants.OUT;
		else if (pos == exit)	return Constants.EXIT;
		else if (isWall(pos))	return Constants.WALL;
		else if (isCoin(pos))	return Constants.COIN;
		else					return Constants.FREE;
	}

	public Integer getUpPos(int cur) {
		int pos = cur - X_LENGTH;
		if( pos < 0 ) return null;
		return pos;
	}
	public Integer getDownPos(int cur)  {
		int pos = cur + X_LENGTH; 
		if( pos >= tableSize ) return null;
		return pos;
	}
	public Integer getLeftPos(int cur)  {
		int pos = cur - 1;
		if( (pos/X_LENGTH) != (cur/X_LENGTH) ) return null;
		return cur - 1;
	}
	public Integer getRightPos(int cur) {
		int pos = cur + 1;
		if( (pos/X_LENGTH) != (cur/X_LENGTH) ) return null;
		return pos;
	}

	public int 	 	getTableSize(){ return tableSize; }
	public Integer 	getExit() 	  { return exit; }
	public Integer 	getEntrance() { return entrance; }
	public List 	getRoad() 	  { return Arrays.asList(road.toArray()); }

	public boolean visited(int pos){
		return blocks[pos].visited;
	}
	public boolean isWall(int pos) { return blocks[pos].wall; }
	public boolean isWall(int x, int y) { return isWall((y * Y_LENGTH) + x); }

	public boolean isCoin(int pos) {
		return blocks[pos].label != null && blocks[pos].label.equals("M");
	}
	public boolean isCoin(int x, int y) { return isCoin((y * Y_LENGTH) + x); }

	public void setMessage(String message) {
		this.message = message;
		super.repaint();
	}

	public void setMessageWR(String message) {
		this.message = message;
	}

	public void setSpot(Color color, int x, int y) throws NoSuchFieldException {
		try {
			setSpot(color, ((y * Y_LENGTH) + x));
		} catch (NoSuchFieldException e) {
			throw new NoSuchFieldException(e.getMessage() + " X:" + x + " Y:" + y);
		}
	}
	public void setSpot(Color color, int pos) throws NoSuchFieldException {
		setSpotWR(color, pos);
		super.repaint();
	}

	/*
	 *	Set spot without repaint
	 */
	public void setSpotWR(Color color, int pos) throws NoSuchFieldException {
		if(blocks[pos].wall == true)
			throw new NoSuchFieldException("You are trying to move inside a wall.\nPOS:" + pos);
		blocks[pos].visited = true;
		blocks[pos].label = null;
		blocks[pos].ball = true;
		blocks[pos].color = color;
	}



	public void setWallSpot(Color color, int x, int y) throws NoSuchFieldException {
		try {
			setWallSpot(color, ((y * Y_LENGTH) + x));
		} catch (NoSuchFieldException e) {
			throw new NoSuchFieldException(e.getMessage() + " X:" + x + " Y:" + y);
		}
	}
	public void setWallSpot(Color color, int pos) throws NoSuchFieldException {
		setWallSpotWR(color, pos);
		super.repaint();
	}
	/*
	 *	Set wall spot without repaint
	 */
	public void setWallSpotWR(Color color, int pos) throws NoSuchFieldException {
		if(blocks[pos].wall == false)
			throw new NoSuchFieldException("You are trying to move a non wall.\nPOS:" + pos);
		blocks[pos].ball = true;
		blocks[pos].color = color;
		blocks[pos].visited = true;
	}

	/*----------------------------------------------------------------------------------------------------------------------------------*/

	public int getXLength() {
		return Constants.WINDOW_HEIGHT;
	}

	public int getYLength() {
		return Constants.WINDOW_WIDTH;
	}

	public Dimension windowSize() {
		return new Dimension(0,0);
	}

	/*----------------------------------------------------------------------------------------------------------------------------------*/

	public void draw(Graphics g) {
		drawMaze(g);
		drawNetwork(g);
	}

	/*----------------------------------------------------------------------------------------------------------------------------------*/

	private void drawMaze(Graphics g) {
		int xCoord, yCoord, xDotBase, yDotBase;
		g.setColor(Color.BLACK);

		for (int i = 0; i < X_LENGTH; i++) {
			int count = i;
			for (int j = 0; j < Y_LENGTH; j++) {
				xCoord = i * Constants.CELL_WIDTH + Constants.MARGIN;
				yCoord = j * Constants.CELL_WIDTH + Constants.MARGIN;
				if (j != 0) { count += X_LENGTH; }
				if (blocks[count].label != null) {
					if(blocks[count].label.equals("M")) {
						xDotBase = i * Constants.CELL_WIDTH + Constants.MARGIN + Constants.DOT_MARGIN;
						yDotBase = j * Constants.CELL_WIDTH + Constants.MARGIN + Constants.DOT_MARGIN;
						g.drawImage(coin, xDotBase-4, yDotBase-4, Constants.DOT_SIZE+10, Constants.DOT_SIZE+10, null);
					} else {
						g.setFont(font);
						g.drawString(blocks[count].label, (i * (Constants.CELL_WIDTH) + (Constants.CELL_WIDTH/3) + Constants.MARGIN), (j * (Constants.CELL_WIDTH) + (Constants.CELL_WIDTH) + Constants.MARGIN));
					} 
				}
				if (blocks[count].ball == true) {
					xDotBase = i * Constants.CELL_WIDTH + Constants.MARGIN + Constants.DOT_MARGIN;
					yDotBase = j * Constants.CELL_WIDTH + Constants.MARGIN + Constants.DOT_MARGIN;
					g.setColor(blocks[count].color);
					g.fillOval(xDotBase, yDotBase, Constants.DOT_SIZE, Constants.DOT_SIZE);
					g.setColor(Color.BLACK);
				}
				if (blocks[count].wall == true) {
					g.drawLine(xCoord, yCoord,
						((i + 1) * Constants.CELL_WIDTH + Constants.MARGIN), (j * Constants.CELL_WIDTH + Constants.MARGIN));
					g.drawLine(xCoord, (j + 1) * Constants.CELL_WIDTH
						+ Constants.MARGIN, (i + 1) * Constants.CELL_WIDTH + Constants.MARGIN, (j + 1) * Constants.CELL_WIDTH
						+ Constants.MARGIN);
					g.drawLine((i + 1) * Constants.CELL_WIDTH + Constants.MARGIN, j * Constants.CELL_WIDTH
						+ Constants.MARGIN, (i + 1) * Constants.CELL_WIDTH + Constants.MARGIN, (j + 1) * Constants.CELL_WIDTH
						+ Constants.MARGIN);
					g.drawLine(xCoord, yCoord, xCoord, (j + 1) * Constants.CELL_WIDTH + Constants.MARGIN);
				}
			}
			// if (!message.equals("")) {
			// 	int lines = 0;
			// 	for(String msg : message.split("\n")) {
			// 		g.drawString(msg, (Constants.MARGIN), ((Y_LENGTH + lines) * (Constants.CELL_WIDTH) + (Constants.CELL_WIDTH) + Constants.MARGIN));
			// 		lines++;
			// 	}
			// }

		}

		g.drawLine(Constants.MARGIN, Constants.MARGIN, (X_LENGTH * Constants.CELL_WIDTH + Constants.MARGIN), (Constants.MARGIN));
		g.drawLine((X_LENGTH * Constants.CELL_WIDTH + Constants.MARGIN), (Y_LENGTH * Constants.CELL_WIDTH + Constants.MARGIN), Constants.MARGIN, (Y_LENGTH * Constants.CELL_WIDTH + (Constants.MARGIN)));
		g.drawLine(Constants.MARGIN, Constants.MARGIN, Constants.MARGIN, (Y_LENGTH * Constants.CELL_WIDTH + (Constants.MARGIN)));
		g.drawLine((X_LENGTH * Constants.CELL_WIDTH + Constants.MARGIN), (Y_LENGTH * Constants.CELL_WIDTH + Constants.MARGIN), (X_LENGTH * Constants.CELL_WIDTH + Constants.MARGIN), (Constants.MARGIN));
	}


	/*----------------------------------------------------------------------------------------------------------------------------------*/

	private void drawNetwork(Graphics g) {
		drawInputLayer(g);
		drawFirstConns(g);
		drawHiddenLayer(g);
		drawSecondConns(g);
		drawOutputLayer(g);
	}


	private String[] inputLayer;
	public void setInputLayer(double[] entries) {
		for(int i = 0; i < entries.length; i++){
			inputLayer[i] = String.valueOf(entries[i]);
			if(inputLayer[i].length() == 3)
				inputLayer[i] = " " + inputLayer[i];
		}
	}
	public void drawInputLayer(Graphics g) {
		int x = 320, y = 30, d = 10, yOffset = 40;

		double[] varx = new double[] {-1.0, 0.0, 0.0, -1.0, 18.0};

		for (int i = 1; i < 6; i++) {
			g.drawString(inputLayer[i-1], x-50, y + 6 +(yOffset*i));
			drawCenteredCircle(g, true, x, y + (yOffset*i), d);
		}
	}

	public void drawFirstConns(Graphics g) {
		int x = 326, x2 = x+70,
			yOffset = 40,
			y1 = 72,
			y2 = y1 + yOffset,
			y3 = y2 + yOffset,
			y4 = y3 + yOffset,
			y5 = y4 + yOffset;

		for(int i = 18; i < 139; i += yOffset){
			LineArrow (g, x, y1, x2, y1 + i, Color.BLACK, 1);
			LineArrow (g, x, y2, x2, y1 + i, Color.BLACK, 1);
			LineArrow (g, x, y3, x2, y1 + i, Color.BLACK, 1);
			LineArrow (g, x, y4, x2, y1 + i, Color.BLACK, 1);
			LineArrow (g, x, y5, x2, y1 + i, Color.BLACK, 1);
		}
	}

	private double[] hidden;
	private double[] output;
	public void setNetwork(NeuralNetwork neuralNetwork) {
		// hidden = neuralNetwork.getHiddenLayer();
		// output = neuralNetwork.getOutputLayer();
	}

	public void drawHiddenLayer(Graphics g) {
		int x = 405, y = 50, d = 30, yOffset = 40;

		for(int i = 1; i < 5; i++)
			drawCenteredCircle(g, false, x, y + (yOffset*i), d);
	}

	public void drawSecondConns(Graphics g) {
		int x = 420, x2 = x+70,
			yOffset = 40,
			y = 72,
			y1 = 90,
			y2 = y1 + yOffset,
			y3 = y2 + yOffset,
			y4 = y3 + yOffset;

		for(int i = 18; i < 139; i += yOffset){
			LineArrow (g, x, y1, x2, y + i, Color.BLACK, 1);
			LineArrow (g, x, y2, x2, y + i, Color.BLACK, 1);
			LineArrow (g, x, y3, x2, y + i, Color.BLACK, 1);
			LineArrow (g, x, y4, x2, y + i, Color.BLACK, 1);
		}
	}
	
	public void drawOutputLayer(Graphics g) {
		String[] letters = new String[] {"U", "D", "L", "R"};
		int x = 495, y = 50, d = 25, yOffset = 40;

		for(int i = 0; i < 4; i++) {
			drawCenteredCircle(g, false, x, y + (yOffset*(i+1)), d);
			g.drawString(letters[i], x-5, y + 9 +(yOffset*(i+1)));
		}
	}

	/*----------------------------------------------------------------------------------------------------------------------------------*/

	public void drawCenteredCircle(Graphics g, boolean fill, int x, int y, int r) {
		Graphics2D g2 = (Graphics2D) g;
		x = x-(r/2);
		y = y-(r/2);
		
		if(fill)
			g2.fillOval(x,y,r,r);
		else
			g2.drawOval(x,y,r,r);
	}

	private static final Polygon ARROW_HEAD = new Polygon();
    
    static {
        ARROW_HEAD.addPoint(0, 0);
        ARROW_HEAD.addPoint(-5, -10);
        ARROW_HEAD.addPoint(5, -10);
    }

    public void LineArrow(Graphics g, int x, int y, int x2, int y2, Color color, int thickness) {
        Graphics2D g2 = (Graphics2D) g;
		AffineTransform defaultTransf = g2.getTransform();
        Stroke  defaultStroke = g2.getStroke();

        //prepare variables
        AffineTransform tx2 = (AffineTransform) defaultTransf.clone();
        int endX = x2;
        int endY = y2;
        double angle = Math.atan2(endY - y, endX - x);

        //prepare arrow
        g2.setColor(color);
        g2.setStroke(new BasicStroke(thickness));
        //draw arrow
        g2.drawLine(x, y, (int) (endX - 10 * Math.cos(angle)), (int) (endY - 10 * Math.sin(angle)));
        // tx2.translate(endX, endY);
        // tx2.rotate(angle - Math.PI / 2);
        g2.setTransform(tx2);
        // g2.fill(ARROW_HEAD);

        //reset formatation
        g2.setTransform(defaultTransf);
        g2.setStroke(defaultStroke);
        g2.setColor(Color.BLACK);
    }
}
