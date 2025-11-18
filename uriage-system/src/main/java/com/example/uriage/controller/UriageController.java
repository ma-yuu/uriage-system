package com.example.uriage.controller;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.uriage.model.Uriage;
import com.example.uriage.repository.UriageRepository;

@Controller
public class UriageController {

    private final UriageRepository repository;

    public UriageController(UriageRepository repository) {
        this.repository = repository;
    }

    // ルートアクセス → ログイン画面
    @GetMapping("/")
    public String loginForm(HttpSession session) {
        if (session.getAttribute("loginUser") != null) {
            return "redirect:/home";
        }
        return "login"; // login.html
    }

    // ログイン処理
    @PostMapping("/login")
    public String login(HttpServletRequest request, String username, String password, Model model) {
        if ("admin".equals(username) && "password".equals(password)) {
            request.getSession().setAttribute("loginUser", username);
            return "redirect:/home";
        } else {
            model.addAttribute("error", "ユーザー名またはパスワードが間違っています");
            return "login";
        }
    }

    // ログアウト
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // 売上管理ページ（ソート対応）
    @GetMapping("/home")
    public String home(HttpSession session, Model model, @RequestParam(required = false) String sort) {
        if (session.getAttribute("loginUser") == null) {
            return "redirect:/";
        }

        List<Uriage> list;

        if ("dateDesc".equals(sort)) {
            list = repository.findAllByOrderByDateDesc();
        } else if ("priceAsc".equals(sort)) {
            list = repository.findAllByOrderByPriceAsc();
        } else if ("priceDesc".equals(sort)) {
            list = repository.findAllByOrderByPriceDesc();
        } else if ("quantityAsc".equals(sort)) {
            list = repository.findAllByOrderByQuantityAsc();
        } else if ("quantityDesc".equals(sort)) {
            list = repository.findAllByOrderByQuantityDesc();
        } else {
            list = repository.findAllByOrderByDateAsc(); // デフォルト
        }

        model.addAttribute("uriageList", list);
        model.addAttribute("uriage", new Uriage());
        return "index";
    }

    // 売上登録
    @PostMapping("/add")
    public String add(@ModelAttribute Uriage uriage, HttpSession session) {
        if (session.getAttribute("loginUser") == null) return "redirect:/";
        repository.save(uriage);
        return "redirect:/home";
    }

    // 売上削除
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("loginUser") == null) return "redirect:/";
        repository.deleteById(id);
        return "redirect:/home";
    }

    // CSV出力
    @GetMapping("/export")
    @ResponseBody
    public void exportCsv(HttpServletResponse response, HttpSession session) throws IOException {
        if (session.getAttribute("loginUser") == null) {
            response.sendRedirect("/");
            return;
        }

        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"uriage.csv\"");

        List<Uriage> list = repository.findAll();

        try (OutputStreamWriter osw = new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8);
             PrintWriter writer = new PrintWriter(osw)) {
            writer.write('\uFEFF'); // BOM
            writer.println("ID,日付,商品名,単価,数量,合計");
            for (Uriage u : list) {
                writer.printf("%d,%s,%s,%d,%d,%d%n",
                        u.getId(),
                        u.getDate(),
                        u.getShohinName(),
                        u.getPrice(),
                        u.getQuantity(),
                        u.getTotal());
            }
            writer.flush();
        }
    }
}
