package condorcet.Utility;

import com.google.gson.Gson;
import condorcet.Enums.ResponseStatus;
import condorcet.Models.Entities.*;
//import condorcet.Models.ResultMark;
import condorcet.Models.TCP.Request;
import condorcet.Models.TCP.Response;
import condorcet.Services.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

import static com.mysql.cj.conf.PropertyKey.logger;

///ядро серверной логики, обрабатывающее запросы клиентов через TCP.
///Основные функции:

///Регистрация/авторизация пользователей.
///Управление рейсами (Flight), пассажирами (Passenger), оценками (UserMark).
///Реализация алгоритма Кондорсе для ранжирования рейсов.


public class ClientThread implements Runnable {
    private Socket clientSocket;
    private Request request;
    private Response response;
    private Gson gson;
    private BufferedReader in;
    private PrintWriter out;


    private UserService userService = new UserService();
  //  private PassengerService passengerService = new PassengerService();
    private PersonDataService personDataService = new PersonDataService();


    public ClientThread(Socket clientSocket) throws IOException {
        response = new Response();
        request = new Request();
        this.clientSocket = clientSocket;
        gson = new Gson();
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        out = new PrintWriter(clientSocket.getOutputStream());
    }

    @Override
    public void run() {
        try {
            while (clientSocket.isConnected()) {
                String message = in.readLine();

                request = gson.fromJson(message, Request.class);
                switch (request.getRequestType()) {
                    case REGISTER: {
                        User user = gson.fromJson(request.getRequestMessage(), User.class);
                        if (userService.findAllEntities().stream().noneMatch(x -> x.getLogin().toLowerCase().equals(user.getLogin().toLowerCase()))) {
                            personDataService.saveEntity(user.getPersonData());
                            userService.saveEntity(user);
                            userService.findAllEntities();
                            User returnUser;
                            returnUser = userService.findEntity(user.getId());
                            response = new Response(ResponseStatus.OK, "Готово!", gson.toJson(returnUser));
                        } else {
                            response = new Response(ResponseStatus.ERROR, "Такой пользователь уже существует!", "");
                        }
                        break;
                    }
                    case LOGIN: {
                        User requestUser = gson.fromJson(request.getRequestMessage(), User.class);
                        if (userService.findAllEntities().stream().anyMatch(x -> x.getLogin().toLowerCase().equals(requestUser.getLogin().toLowerCase())) && userService.findAllEntities().stream().anyMatch(x -> x.getPassword().equals(requestUser.getPassword()))) {
                            User user = userService.findAllEntities().stream().filter(x -> x.getLogin().toLowerCase().equals(requestUser.getLogin().toLowerCase())).findFirst().get();
                            user = userService.findEntity(user.getId());
                            response = new Response(ResponseStatus.OK, "Готово!", gson.toJson(user));
                        } else {
                            response = new Response(ResponseStatus.ERROR, "Такого пользователя не существует или неправильный пароль!", "");
                        }
                        break;
                    }
                    case DISPLAY_USER_DATA: {
                        try {
                            List<User> users = userService.findAllEntities();
                            users.forEach(this::cleanUserRelations);// аналогично users.forEach(user -> cleanUserRelations(user));
                            response = new Response(ResponseStatus.OK, "Данные пользователей", gson.toJson(users));
                        } catch (Exception e) {
                            response = new Response(ResponseStatus.ERROR, "Ошибка получения данных: " + e.getMessage(), "");
                        }
                        break;
                    }

                    case DELETE_USER: {
                        try {
                            User userToDelete = gson.fromJson(request.getRequestMessage(), User.class);
                            User user = userService.findEntity(userToDelete.getId());

                            if (user == null) {
                                response = new Response(ResponseStatus.ERROR, "Пользователь не найден", "");
                                break;
                            }

                            PersonData personData = user.getPersonData();
                            userService.deleteEntity(user); // Удаляем пользователя

                            // Проверяем, есть ли другие пользователи с этими PersonData
                            List<User> usersWithSamePersonData = userService.findAllEntities().stream()
                                    .filter(u -> u.getPersonData() != null && u.getPersonData().getId() == personData.getId())
                                    .collect(Collectors.toList());

                            if (usersWithSamePersonData.isEmpty()) {
                                // Проверяем, существует ли PersonData
                                PersonData pdFromDb = personDataService.findEntity(personData.getId());
                                if (pdFromDb != null) {
                                    personDataService.deleteEntity(pdFromDb); // Удаляем, только если существует
                                }
                            }

                            response = new Response(ResponseStatus.OK, "Пользователь успешно удален", "");
                        } catch (Exception e) {
                            System.out.println("Ошибка при удалении пользователя: " + e.getMessage());
                            response = new Response(ResponseStatus.ERROR, "Ошибка удаления: " + e.getMessage(), "");
                        }
                        break;
                    }
                    case UPDATE_USER: {
                        try {
                            User updatedUser = gson.fromJson(request.getRequestMessage(), User.class);
                            User existingUser = userService.findEntity(updatedUser.getId());

                            if (existingUser == null) {
                                response = new Response(ResponseStatus.ERROR, "Пользователь не найден", "");
                                break;
                            }

                            // Обновление PersonData
                            if (existingUser.getPersonData() == null) {
                                response = new Response(ResponseStatus.ERROR, "У пользователя нет PersonData", "");
                                break;
                            }

                            // Сохраняем ID существующей PersonData
                            updatedUser.getPersonData().setId(existingUser.getPersonData().getId());

                            // Обновляем PersonData
                            personDataService.updateEntity(updatedUser.getPersonData());

                            // Обновляем User (кроме пароля, если он не был изменен)
                            if (updatedUser.getPassword() == null || updatedUser.getPassword().isEmpty()) {
                                updatedUser.setPassword(existingUser.getPassword());
                            }

                            userService.updateEntity(updatedUser);

                            // Возвращаем обновленные данные
                            User cleanUser = userService.findEntity(updatedUser.getId());
                            cleanUserRelations(cleanUser); // Очищаем связи, если необходимо

                            response = new Response(ResponseStatus.OK, "Данные обновлены", gson.toJson(cleanUser));
                        } catch (Exception e) {
                            response = new Response(ResponseStatus.ERROR, "Ошибка обновления: " + e.getMessage(), "");
                        }
                        break;
                    }
                    case GET_USER_BY_ID: {
                        try {
                            int userId = Integer.parseInt(request.getRequestMessage());
                            User user = userService.findEntity(userId);

                            if (user != null) {
                                cleanUserRelations(user); // Очистите связи при необходимости
                                response = new Response(ResponseStatus.OK, "", gson.toJson(user));
                            } else {
                                response = new Response(ResponseStatus.ERROR, "Пользователь не найден", "");
                            }
                        } catch (Exception e) {
                            response = new Response(ResponseStatus.ERROR, "Ошибка: " + e.getMessage(), "");
                        }
                        break;
                    }

                }
                out.println(gson.toJson(response));
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Клиент " + clientSocket.getInetAddress() + ":" + clientSocket.getPort() + " закрыл соединение.");
            try {

                clientSocket.close();
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void cleanUserRelations(User user) {
        if (user.getPersonData() != null) {
            user.getPersonData().setUsers(null);
            user.getPersonData().setPassengers(null);
        }
    }

}
