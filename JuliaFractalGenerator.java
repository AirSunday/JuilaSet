import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

public class JuliaFractalGenerator {

    public static void main(String[] args) {
        int width = 4096; // Значение по умолчанию для ширины
        int height = 4096; // Значение по умолчанию для высоты
        double realPart = -0.75; // Значение по умолчанию для действительной части
        double imaginaryPart = 0.1; // Значение по умолчанию для мнимой части
        String outputFile = "1.png"; // Значение по умолчанию для выходного файла

        if (args.length == 6) {
            width = Integer.parseInt(args[1].split(";")[0]);
            height = Integer.parseInt(args[1].split(";")[1]);
            realPart = Double.parseDouble(args[3].split(";")[0]);
            imaginaryPart = Double.parseDouble(args[3].split(";")[1]);
            outputFile = args[5];
        } else {
            // Выводим предупреждение о том, что используются значения по умолчанию
            System.out.println("Using default values. Usage: java JuliaFractalGenerator -d <Width>;<Height> -c <Real-part>;<Imaginary-part> -o <output-file>");
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int processors = Runtime.getRuntime().availableProcessors(); // получаем количество доступных процессоров
        ExecutorService executorService = Executors.newFixedThreadPool(processors); // пул потоков для расчета фрактала
        List<CompletableFuture<Void>> futures = new ArrayList<>();// список для отслеживания выполнения задач

        for (int i = 0; i < processors; i++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(
                    new JuliaFractalTask(image, width, height, realPart, imaginaryPart, i, processors),
                    executorService
            );
            futures.add(future);
        }

        // Ожидаем завершения всех задач
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        try {
            allOf.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        try {
            ImageIO.write(image, "png", new File(outputFile));
            System.out.println("Image saved to " + outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        executorService.shutdown();
    }
}

class JuliaFractalTask implements Runnable {
    private BufferedImage image;
    private int width;
    private int height;
    private double realPart;
    private double imaginaryPart;
    private int startRow;
    private int step;

    public JuliaFractalTask(BufferedImage image, int width, int height, double realPart, double imaginaryPart, int startRow, int step) {
        this.image = image;
        this.width = width;
        this.height = height;
        this.realPart = realPart;
        this.imaginaryPart = imaginaryPart;
        this.startRow = startRow;
        this.step = step;
    }

    @Override
    public void run() {
        for (int y = startRow; y < height; y += step) {
            for (int x = 0; x < width; x++) {
                int color = calculatePixelColor(x, y);
                image.setRGB(x, y, color);
            }
        }
    }

    private int calculatePixelColor(int x, int y) {

        int maxIterations = 600; // Максимальное количество итераций
        double minReal = -2.0; // Минимальное значение действительной части
        double maxReal = 2.0; // Максимальное значение действительной части
        double minImaginary = -2.0; // Минимальное значение мнимой части
        double maxImaginary = 2.0; // Максимальное значение мнимой части

        double zx = x * (maxReal - minReal) / width + minReal;
        double zy = y * (maxImaginary - minImaginary) / height + minImaginary;

        int iterations = 0;
        while (iterations < maxIterations && (zx * zx + zy * zy) < 4) {
            double xTemp = zx * zx - zy * zy + realPart;
            zy = 2 * zx * zy + imaginaryPart;
            zx = xTemp;
            iterations++;
        }

        //Настройка цвета (черный или голубой)
        double hue = (double) iterations / maxIterations;
        int r = (int) (hue * 255);
        int g = (int) (hue * 255);
        int b = (int) (hue * 255);

        if(hue > 0.5) {
            r = (int) hue;
        }

        return new Color(r, g, b).getRGB();
    }
}
