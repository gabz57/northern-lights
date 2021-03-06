import {InjectionKey} from 'vue'
import {initialState, State} from './state'
import {CommitOptions, createStore, DispatchOptions, Store as VuexStore, useStore as baseUseStore} from 'vuex'
import {Mutations, mutations} from "@/store/mutations";
import {Actions, actions} from "@/store/actions";
import {Getters, getters} from "@/store/getters";

const getDefaultState = () => {
    return initialState
}

export const store = createStore<State>({
    state: getDefaultState(),
    mutations: mutations,
    actions: actions,
    getters: getters,
    modules: {}
})

export const key: InjectionKey<VuexStore<State>> = Symbol()

// define your own `useStore` composition function
export function useStore() {
    return baseUseStore(key) as Store
}

export type Store = Omit<VuexStore<State>,
    'getters' | 'commit' | 'dispatch'> & {
    commit<K extends keyof Mutations, P extends Parameters<Mutations[K]>[1]>(
        key: K,
        payload: P,
        options?: CommitOptions
    ): ReturnType<Mutations[K]>
} & {
    dispatch<K extends keyof Actions>(
        key: K,
        payload?: Parameters<Actions[K]>[1],
        options?: DispatchOptions
    ): ReturnType<Actions[K]>
} & {
    getters: {
        [K in keyof Getters]: ReturnType<Getters[K]>
    }
}
