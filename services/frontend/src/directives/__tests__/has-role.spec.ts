import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { vRole } from '../has-role.ts'
import { nextTick } from 'vue'

vi.mock('@/stores/authStore', () => ({
  useAuthStore: () => ({
    roles: ['admin', 'user'],
  }),
}))

const TestComponent = {
  template: `<button v-role="role">Click Me</button>`,
  props: ['role'],
}

describe('vRole directive', () => {
  beforeEach(() => {
    document.body.innerHTML = ''
  })

  it('renders normally if user has the required role', () => {
    const wrapper = mount(TestComponent, {
      global: { directives: { role: vRole } },
      props: { role: 'admin' },
    })
    const button = wrapper.find('button')
    expect(button.exists()).toBe(true)
    expect(button.attributes('aria-disabled')).toBeUndefined()
    expect(button.element.parentElement?.tagName.toLowerCase()).not.toBe('span')
  })

  it('disables and wraps element if user lacks required role', async () => {
    vi.doMock('@/stores/authStore', () => ({
      useAuthStore: () => ({ roles: ['user'] }),
    }))
    const { vRole: vRoleAlt } = await import('../has-role.ts')

    const wrapper = mount(TestComponent, {
      global: { directives: { role: vRoleAlt } },
      props: { role: 'root' },
    })

    await nextTick() // Let Vue apply directive and update DOM

    const button = wrapper.find('button')
    expect(button.exists()).toBe(true)

    const parent = button.element.parentElement
    expect(parent?.tagName.toLowerCase()).toBe('span')
    expect(parent?.getAttribute('title')).toContain('Requires role: root')

    expect(button.attributes('aria-disabled')).toBe('true')
  })
  it('supports multiple required roles', async () => {
    const wrapper = mount(TestComponent, {
      global: { directives: { role: vRole } },
      props: { role: ['manager', 'user'] },
    })
    const button = wrapper.find('button')
    expect(button.attributes('aria-disabled')).toBeUndefined()
  })
  it('matches roles case-insensitively', () => {
    const wrapper = mount(TestComponent, {
      global: { directives: { role: vRole } },
      props: { role: 'AdMiN' },
    })
    const button = wrapper.find('button')
    expect(button.attributes('aria-disabled')).toBeUndefined()
  })
})
