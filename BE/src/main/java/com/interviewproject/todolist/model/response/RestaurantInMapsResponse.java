package com.interviewproject.todolist.model.response;

import java.io.Serializable;
import java.util.Objects;

import lombok.*;
import java.util.Set;

import com.interviewproject.todolist.model.entity.RestaurantImage;

import java.util.Map;

@Getter
@Setter
public class RestaurantInMapsResponse implements Serializable {
    private Long maSoNhaHang;
    private String url;
    private String ten;
    private String diaChi;
    private String loaiHinh;
    private String khoangGia;
    private String gioHoatDong;
    private String phuHop;
    private String monDacSac;
    private String moTaKhongGian;
    private String diemDacTrung;
    private Double viDo;
    private Double kinhDo;
    private String thanhPho;
    private Map<String, Set<String>> imageUrls; // Chứa các ảnh theo loại

    @Override
    public String toString() {
        return "RestaurantInMapsResponse{" +
                "MaSoNhaHang=" + maSoNhaHang +
                ", URL='" + url + '\'' +
                ", Ten='" + ten + '\'' +
                ", DiaChi=" + diaChi +
                '}';
    }

    public Long getMaSoNhaHang() {
        return maSoNhaHang;
    }

    public void setMaSoNhaHang(Long maSoNhaHang) {
        this.maSoNhaHang = maSoNhaHang;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getLoaiHinh() {
        return loaiHinh;
    }

    public void setLoaiHinh(String loaiHinh) {
        this.loaiHinh = loaiHinh;
    }

    public String getKhoangGia() {
        return khoangGia;
    }

    public void setKhoangGia(String khoangGia) {
        this.khoangGia = khoangGia;
    }

    public String getGioHoatDong() {
        return gioHoatDong;
    }

    public void setGioHoatDong(String gioHoatDong) {
        this.gioHoatDong = gioHoatDong;
    }

    public String getPhuHop() {
        return phuHop;
    }

    public void setPhuHop(String phuHop) {
        this.phuHop = phuHop;
    }

    public String getMonDacSac() {
        return monDacSac;
    }

    public void setMonDacSac(String monDacSac) {
        this.monDacSac = monDacSac;
    }

    public String getMoTaKhongGian() {
        return moTaKhongGian;
    }

    public void setMoTaKhongGian(String moTaKhongGian) {
        this.moTaKhongGian = moTaKhongGian;
    }

    public String getDiemDacTrung() {
        return diemDacTrung;
    }

    public void setDiemDacTrung(String diemDacTrung) {
        this.diemDacTrung = diemDacTrung;
    }

    public Double getViDo() {
        return viDo;
    }

    public void setViDo(Double viDo) {
        this.viDo = viDo;
    }

    public Double getKinhDo() {
        return kinhDo;
    }

    public void setKinhDo(Double kinhDo) {
        this.kinhDo = kinhDo;
    }

    public String getThanhPho() {
        return thanhPho;
    }

    public void setThanhPho(String thanhPho) {
        this.thanhPho = thanhPho;
    }

    public Map<String, Set<String>> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(Map<String, Set<String>> imageUrls) {
        this.imageUrls = imageUrls;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RestaurantInMapsResponse that = (RestaurantInMapsResponse) o;
        return Objects.equals(maSoNhaHang, that.maSoNhaHang) &&
                Objects.equals(url, that.url) &&
                Objects.equals(ten, that.ten) &&
                Objects.equals(diaChi, that.diaChi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maSoNhaHang, url, ten, diaChi);
    }

}
