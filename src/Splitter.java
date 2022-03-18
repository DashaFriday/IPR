import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Splitter {

    private static String source_file;
    private static String path_to_new;
    private static long lines_count;

    public static void split_it(long lines_all, int count_of_parts, String path_to_file, String path_to_dir) throws IOException {

        //открыаем файл для считывания строк
        BufferedReader reader_file = new BufferedReader(new FileReader(path_to_file));

        //вытягиваем постоянную часть имени в переменную
        String[] temp_array = path_to_dir.split("\\\\");
        String static_part_of_name = temp_array[temp_array.length-1];

        //считаем сколько на каждый файл приходится строк
        long lines_in_one = lines_all / count_of_parts;

        //для каждого нового файла прогоняем код
        for (int i = 1; i <= count_of_parts; i++) {

            //создаём райтер в новый файл с номером по i
            BufferedWriter writer = new BufferedWriter(new FileWriter(path_to_dir + "\\" + static_part_of_name + "_" + i + ".txt"));

            //пока счётчик меньше чем количество строк в файл записываем строки в файл
            for (int j = 1; j <= lines_in_one; j++) {
                writer.append(reader_file.readLine() + "\n");
            }

            //если строк осталось больше, чем необходимо в 1 файл,
            //то вычитаем из общего количества количество на 1 файл
            //иначе записываем оставшиеся в последний файл
            if (lines_all >= lines_in_one) {
                lines_all = lines_all - lines_in_one;
            } else {
                for (int j = 1; j <= lines_all; j++) {
                    writer.append(reader_file.readLine() + "\n");
                }
            }

            //
            writer.close();

        }

        //
        reader_file.close();

    }

    public static void main(String[] args) throws IOException {

        System.out.println("SPLITTER");

        //читаем файл, проверяем его существование, получаем счётчик строк и путь к исходнику в переменные
        try {
            //получаем в переменную путь к исходному файлу
            String source_file = args[0];

            //создаём директорию по заданному пути и получаем путь к новой директории в переменную
            String path_to_new = args[1];
            new File(path_to_new).mkdir();

            //считаем строки файла за 1 проход
            Stream<String> lines =
                    Files.lines(Paths.get(source_file));

            lines_count = lines.count();

            //решаем на сколько файлов делим исходник
            if (lines_count < 10) {
                split_it(lines_count, (int) lines_count, source_file, path_to_new);
            } else if (lines_count < 1000) {
                split_it(lines_count, 5, source_file, path_to_new);
            } else {
                split_it(lines_count, 10, source_file, path_to_new);
            }

            System.out.println("DONE");

            lines.close();
        } catch (FileNotFoundException e) {
            System.out.println("Файл с таким именем не найден. Попробуйте снова");
        } catch (java.nio.file.NoSuchFileException e) {
            System.out.println("Файл с таким именем не найден. Попробуйте снова");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UncheckedIOException e) {
            System.out.println("Неизвестный формат файла, попробуйте ещё раз");
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Неверные параметры или их количество");
        }

    }

}
