package com.gromsoft.onlyroad;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

//Слой трека с отображением где производилась запись другим цветом
public class RouteOverlay extends ItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>(); // Массив элементов слоя
	private int color;
	private boolean recording = false;
	private int deltaX, deltaY;

	// Конструктор не стандартный, а с контекстом для обработки событий
	public RouteOverlay(Drawable defaultMarker, int color) {
		super(boundCenterBottom(defaultMarker)); // Основная картинка-маркер для каждого слоя. Центр - снизу посередине
		this.color = color;
	}

	public void setRecording(boolean state) {
		recording = state;
	}

	public void addOverlay(OverlayItem item) {
		mOverlays.add(item);// Добавить в массив элементов
		populate();// Подготовить к прорисовке элемента, вызывает createItem
	}

	public void clearItems() {
		mOverlays.clear();
	}

	@Override
	protected OverlayItem createItem(int overlayIndex) {

		return mOverlays.get(overlayIndex);// populate() должен получить элемент для прорисовки
	}

	@Override
	public int size() {
		return mOverlays.size(); // Количество элементов на слое
	}

	@Override
	protected boolean onTap(int index) {
		return true;
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {

		Projection projection = mapView.getProjection();// Конвертер из географических координат в координаты MapView

		Paint paint = new Paint();
		paint.setColor(color);
		paint.setStrokeWidth(5);
		paint.setAlpha(120);

		GeoPoint gp1, gp2;

		Point point1 = new Point();
		Point point2 = new Point();

		int itemNumber = mOverlays.size();

		if (itemNumber > 1) { // первую точку задаем не в локейшн лисенере, а когда создаем слой
			for (int i = 1; i < itemNumber; i++) {

				gp1 = mOverlays.get(i - 1).getPoint();
				gp2 = mOverlays.get(i).getPoint();

				projection.toPixels(gp1, point1);
				projection.toPixels(gp2, point2);

				// TODO попробовать добавлять точки в массив (они еще понадобятся для сохранения видео), а тут только транслировать в координаты
				// MapView и выводить потом пачкой линий canvas.drawLines(массив, паинт)
				if(recording)canvas.drawLine(point1.x, point1.y, point2.x, point2.y, paint);
			}
		}

		deltaX = point2.x - point1.x;
		deltaY = point2.y - point1.y;

		// super.draw(canvas, mapView, shadow);
	}

	public float getAngle() {

		return (float) Math.toDegrees(Math.atan2(deltaY, deltaX));
	}

}
