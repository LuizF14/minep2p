package br.edu.unifei.minep2p.service.controllers;

public abstract class BaseController<T> {
    public abstract String execute(T arg) throws Exception;
}
