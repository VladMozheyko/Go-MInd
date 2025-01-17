package com.example.gomind.adapters;

// Импорт необходимых библиотек
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gomind.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

// Адаптер для RecyclerView, отображающий список ответов
public class AnswersAdapter extends RecyclerView.Adapter<AnswersAdapter.AnswerViewHolder> {

    private List<String> answers; // Список ответов (например, "A", "B", "C", "D")

    // Интерфейс для обработки нажатий на ответы
    public interface AnswerClickListener {
        void onClickListener(int answerId); // Метод, вызываемый при нажатии
    }

    private AnswerClickListener clickListener; // Обработчик нажатий
    private int selectedItemIndex = -1; // Индекс выбранного элемента (-1, если ничего не выбрано)

    // Конструктор, принимающий список ответов и обработчик нажатий
    public AnswersAdapter(List<String> answers, AnswerClickListener clickListener) {
        this.answers = answers; // Инициализация списка ответов
        this.clickListener = clickListener; // Инициализация обработчика
    }

    @Override
    public AnswerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Создаем макет для элемента списка (одной кнопки)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_item, parent, false);
        return new AnswerViewHolder(view); // Возвращаем новый ViewHolder
    }

    @Override
    public void onBindViewHolder(AnswerViewHolder holder, int position) {
        // Устанавливаем текст ответа на кнопке
        holder.answerButton.setText(answers.get(position));

        // Настройка фона кнопки: выделенный или обычный
        if (position == selectedItemIndex) {
            holder.answerButton.setBackgroundResource(R.drawable.auth_button); // Фон для выбранного элемента
        } else {
            holder.answerButton.setBackgroundResource(R.drawable.border_inside); // Фон для остальных элементов
        }

        // Обработчик нажатия на кнопку
        holder.answerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getAdapterPosition(); // Получаем текущую позицию элемента

                if (currentPosition == RecyclerView.NO_POSITION) {
                    return; // Если позиция недействительна, просто выходим
                }

                if (currentPosition == selectedItemIndex) {
                    // Если элемент уже выбран, снимаем выбор
                    selectedItemIndex = -1;
                } else {
                    // Устанавливаем текущий элемент как выбранный
                    selectedItemIndex = currentPosition;
                }

                // Уведомляем слушателя о нажатии и обновляем список
                clickListener.onClickListener(position + 1); // Передаем индекс ответа (+1 для корректного отображения)
                notifyDataSetChanged(); // Обновляем адаптер, чтобы отобразить изменения
            }
        });
    }

    @Override
    public int getItemCount() {
        return answers.size(); // Возвращаем количество элементов в списке
    }

    // ViewHolder для хранения ссылок на элементы разметки
    public static class AnswerViewHolder extends RecyclerView.ViewHolder {
        MaterialButton answerButton; // Кнопка для отображения ответа

        public AnswerViewHolder(View itemView) {
            super(itemView);
            // Привязываем кнопку из макета
            answerButton = itemView.findViewById(R.id.btn_long_answer);
        }
    }
}
