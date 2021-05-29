import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        //Выбираем класс для сортировки
        String elementClass = "5";
        try
        {
            //Флаг для провеки на последнюю итерацию
            boolean isLast = false;
            do  {
                BufferedReader file = new BufferedReader(new FileReader("file.txt"));
                FileWriter temp_file1 = new FileWriter("file1.txt");
                FileWriter temp_file2 = new FileWriter("file2.txt");
                //Разделяем элементы по файлам (1 фаза)
                isLast = divideElements(file, temp_file1, temp_file2, elementClass);
                FileWriter fileWriter = new FileWriter("file.txt");
                BufferedReader tempFileReader1 = new BufferedReader(new FileReader("file1.txt"));
                BufferedReader tempFileReader2 = new BufferedReader(new FileReader("file2.txt"));
                //Сливаем элементы в один файл (2 фаза)
                mergeElements(tempFileReader1, tempFileReader2, fileWriter);
            }
            while (!isLast);
        }
        catch(IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    //Получаем молярную массу из строки
    public static String getEl(String line) {
        if(line == null) {
            return "`";
        }
        return line.split(" ")[2];
    }


    public static Boolean divideElements(BufferedReader file, FileWriter tempFile1, FileWriter tempFile2,
                                         String elementClass) throws IOException {
        //Шаблон для пустой строки
        String EMPTY_STR = "` ` `\n";
        //Помещаем файлы в массив для быстрого переключения между ними
        FileWriter[] tempFiles = {tempFile1, tempFile2};
        //Индекс текущего файла для записи
        int tempI = 0;
        String line = file.readLine();
        int prevEl = Integer.parseInt(getEl(line));
        int el;
        //Массив для подсчета серий в каждом файле, необходимо для проверки на последнию итерацию
        //Счетчик обновляется только при начале новой серии,
        //Запись начинается в превый файл, поэтому в первом файле количество серий >= 1
        int[] num_series = {1, 0};

        while (line != null ) {
            el = Integer.parseInt(getEl(line));
            //Проверка на класс, если класс элемента не тот, шаг цикла пропускается
            if(!line.split(" ")[0].equals(elementClass)) {
                prevEl = el;
                line = file.readLine();
                continue;
            }
            //Если текущий элемент больше предыдущего, то нужно записывать серию в другой файл
            if (prevEl > el) {
                num_series[tempI]++;
                tempFiles[tempI].write(EMPTY_STR);
                tempFiles[tempI].flush();
                tempI = (tempI + 1) % 2;
            }
            tempFiles[tempI].write(line+'\n');
            tempFiles[tempI].flush();
            prevEl = el;
            line = file.readLine();
        }
        tempFiles[tempI].write(EMPTY_STR);
        tempFiles[tempI].flush();
        if(num_series[0] <= 1 && num_series[1] <= 1) {
            return true;
        }
        return false;
    }

    public static void mergeElements(BufferedReader tempFile1, BufferedReader tempFile2, FileWriter outFile) throws IOException {
        String line1, line2, el1, el2;
        line1 = tempFile1.readLine();
        line2 = tempFile2.readLine();
        while(line1 != null || line2 != null) {
            el1 = getEl(line1);
            el2 = getEl(line2);
            if(el1.equals("`") && el2.equals("`")) {
                line1 = tempFile1.readLine();
                line2 = tempFile2.readLine();
            }
            else if(el1.equals("`")) {
                outFile.write(line2+'\n');
                outFile.flush();
                line2 = tempFile2.readLine();
            }
            else if(el2.equals("`")) {
                outFile.write(line1+'\n');
                outFile.flush();
                line1 = tempFile1.readLine();
            }
            else if(Integer.parseInt(el1) <= Integer.parseInt(el2)) {
                outFile.write(line1+'\n');
                outFile.flush();
                line1 = tempFile1.readLine();
            }
            else  {
                outFile.write(line2+'\n');
                outFile.flush();
                line2 = tempFile2.readLine();
            }
        }

    }
}
